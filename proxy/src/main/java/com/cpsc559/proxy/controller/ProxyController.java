package com.cpsc559.proxy.controller;

import com.cpsc559.proxy.config.PropertiesConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Controller for proxy endpoints
 */
@RestController
public class ProxyController {

    private static final Logger logger = LoggerFactory.getLogger(ProxyController.class);

    private final WebClient webClient;
    private final PropertiesConfig propertiesConfig;

    public ProxyController(PropertiesConfig propertiesConfig, WebClient webClient) {
        this.propertiesConfig = propertiesConfig;
        this.webClient = webClient;
    }

    /**
     * All requests go through here. Forwards requests to the primary server.
     */
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
    @RequestMapping("/**")
    public Mono<ResponseEntity<String>> proxyRequest(HttpServletRequest request, @RequestBody(required = false) String body) {
        // Added this in case we add a search bar
        String queryString = request.getQueryString();

        // Get the full path ex: /api/todolists, /api/todolist/{id} ...
        String requestPath = request.getRequestURI();

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

        return Mono.defer(() -> {
            // Construct the correct path to the primary server
            String targetUrl = propertiesConfig.getUrl() + requestPath +  (queryString != null ? "?" + queryString : "");
            logger.info("Forwarding request to primary at: {}", targetUrl);

            // Build the request object and send it
            return webClient
                    .method(method)                                                     // Set HTTP method
                    .uri(targetUrl)                                                     // Set target URL
                    .headers(httpHeaders -> httpHeaders.addAll(headers))    // Add headers
                    .bodyValue(body != null ? body : "")                                // Set request body
                    .retrieve()                                                         // Send request
                    .toEntity(String.class);})                                          // Parse response
                .retryWhen(
                    Retry.backoff(5, Duration.ofMillis(500)) // Retry 5 times with exponential backoff
                            .doBeforeRetry(retrySignal -> logger.warn("Retrying request {} (attempt {} of 5)", requestPath, retrySignal.totalRetries() + 1))
                            .filter(throwable -> // Only retry on timeouts and connection errors
                                    throwable instanceof TimeoutException || throwable instanceof WebClientRequestException
                            )
                            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                                logger.info("Server did not respond after 5 retries, dropping request to {}", requestPath);
                                throw new RuntimeException("Server did not respond");
                            }))
                .onErrorResume(ignore -> Mono.empty());
    }

    /**
     * POST /updatePrimary - update which server to point to
     * @param primaryUrl New primary server url to point to
     */
    @PostMapping("/updatePrimary")
    public ResponseEntity<?> register(@RequestBody String primaryUrl) {
        logger.info("Updating primary to {}", primaryUrl);
        propertiesConfig.setUrl(primaryUrl);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /updatePrimary - get the current primary server, for debugging purposes
     */
    @GetMapping("/getPrimary")
    public ResponseEntity<?> register() {
        return ResponseEntity.ok(propertiesConfig.getUrl());
    }
}
