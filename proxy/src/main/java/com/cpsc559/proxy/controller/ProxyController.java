package com.cpsc559.proxy.controller;

import com.cpsc559.proxy.config.PropertiesConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class ProxyController {

    private final WebClient webClient;
    private final PropertiesConfig propertiesConfig;

    public ProxyController(PropertiesConfig propertiesConfig, WebClient webClient) {
        this.propertiesConfig = propertiesConfig;
        this.webClient = webClient;
    }

    // All requests go through here
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
    @RequestMapping("/**")
    public Mono<ResponseEntity<String>> proxyRequest(HttpServletRequest request, @RequestBody(required = false) String body) {
        // Added this in case we add a search bar
        String queryString = request.getQueryString();

        // Get the full path ex: /api/todolists, /api/todolist/{id} ...
        String requestPath = request.getRequestURI();

        // Construct the correct path to the primary server
        String targetUrl = propertiesConfig.getUrl() + requestPath +  (queryString != null ? "?" + queryString : "");

        // Create HTTP headers to forward necessary headers (e.g., Content-Type, Authorization)
        HttpHeaders headers = new HttpHeaders();
        if (request.getContentType() != null) {
            headers.setContentType(MediaType.valueOf(request.getContentType()));
        }
        // Add Auth header for JWT token
        if (request.getHeader("Authorization") != null) {
            headers.set("Authorization", request.getHeader("Authorization"));
        }

        // Get the HTTP method from the initial request (GET, PUT, POST, DELETE)
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        // Build the request object and send it
        return webClient
                .method(method)                                                                 // Set HTTP method
                .uri(targetUrl)                                                                 // Set target URL
                .headers(httpHeaders -> httpHeaders.addAll(headers))                // Add headers
                .bodyValue(body != null ? body : "")                                            // Set request body
                .retrieve()                                                                     // Send request
                .toEntity(String.class);                                                        // Parse response
    }

    // POST /updatePrimary - update which server to point to
    @PostMapping("/updatePrimary")
    public ResponseEntity<?> register(@RequestBody String primaryUrl) {
        // Update the primary url
        propertiesConfig.setUrl(primaryUrl);
        return ResponseEntity.ok().build();
    }

    // GET /updatePrimary - get the current primary server, for debugging purposes
    @GetMapping("/getPrimary")
    public ResponseEntity<?> register() {
        return ResponseEntity.ok(propertiesConfig.getUrl());
    }
}
