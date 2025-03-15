package com.cpsc559.proxy.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
public class ProxyController {

    private final WebClient webClient;

    // Base URL for the primary backend server -> defined in application.properties
    @Value("${backend.primary.url}")
    private String primaryBackendUrl;

    // List of comma-seperated URLs for the backup servers -> defined in application.properties
    @Value("${backend.backups.urls}")
    private String[] backupBackendUrls;

    public ProxyController(WebClient webClient) {
        this.webClient = webClient;
    }

    // All requests go through here
    @RequestMapping("/**")
    public Mono<ResponseEntity<String>> proxyRequest(HttpServletRequest request, @RequestBody(required = false) String body) {
        // Added this in case we add a search bar
        String queryString = request.getQueryString();

        // Get the full path after "/api/api" ex: /todolists, todolist/{id} ...
        String requestPath = request.getRequestURI().substring("/api/api".length());

        // Construct the correct path to the primary server
        String targetUrl = primaryBackendUrl + requestPath +  (queryString != null ? "?" + queryString : "");

        // Create HTTP headers to forward necessary headers (e.g., Content-Type, Authorization)
        HttpHeaders headers = new HttpHeaders();
        if (request.getContentType() != null) {
            headers.setContentType(MediaType.valueOf(request.getContentType()));
        }
        // Add Auth header for JWT token
        if (request.getHeader("Authorization") != null) {
            headers.set("Authorization", request.getHeader("Authorization"));
        }
        // Construct the Http request entity (headers + body)
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // Get the HTTP method from the initial request (GET, PUT, POST, DELETE)
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        // Build the request object and send it
        Mono<ResponseEntity<String>> primaryResponse = webClient
                .method(method)                                                                 // Set HTTP method
                .uri(targetUrl)                                                                 // Set target URL
                .headers(httpHeaders -> httpHeaders.addAll(headers))                // Add headers
                .bodyValue(body != null ? body : "")                                            // Set request body
                .retrieve()                                                                     // Send request
                .toEntity(String.class);                                                        // Parse response

        // For write operations, replicate to backup servers (asynchronously)
        // If write operation, replicate to backups
        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE) {
            replicateToBackups(requestPath, queryString, body, headers, method);
        }

        return primaryResponse;
    }

    // Helper function to replicate the request to all backup servers
    private void replicateToBackups(String requestPath, String queryString, String body, HttpHeaders headers, HttpMethod method) {
        List<String> backupUrls = Arrays.asList(backupBackendUrls);

        // Flux.fromIterable helps us make these replication requests in parallel, so each request is non-blocking
        Flux.fromIterable(backupUrls)
                .flatMap(backup -> {
                    String backupUrl = backup + requestPath + (queryString != null ? "?" + queryString : "");
                    return webClient
                            .method(method)
                            .uri(backupUrl)
                            .headers(httpHeaders -> httpHeaders.addAll(headers))
                            .bodyValue(body != null ? body : "")
                            .retrieve()
                            .bodyToMono(String.class)
                            .doOnError(e -> System.err.println("Error replicating to backup " + backup + ": " + e.getMessage()));
                })
                .subscribe(); // Asynchronous execution
    }
}
