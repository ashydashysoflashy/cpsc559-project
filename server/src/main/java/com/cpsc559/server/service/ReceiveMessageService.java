package com.cpsc559.server.service;

import com.cpsc559.server.message.UpdateMessage;
import com.cpsc559.server.sync.LogicalClock;
import com.cpsc559.server.sync.UpdateQueue;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.ContentCachingRequestWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;

@Service
public class ReceiveMessageService extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ReceiveMessageService.class);

    @Value("${server.urls:}")
    private String[] backupUrls;

    @Autowired
    private ElectionService electionService;

    @Autowired
    private UpdateQueue updateQueue;

    @Autowired
    UpdateLogService updateLogService;

    private final WebClient webClient;
    @Autowired
    private LogicalClock logicalClock;

    public ReceiveMessageService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Wrap the request so the body can be read multiple times.
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);

        boolean currentServerIsPrimary = electionService.isLeader();
        boolean requestIsWriteOperation = isWriteOperation(cachingRequest);
        boolean requestIsCatchUpReplay = cachingRequest.getHeader("X-Catchup") != null;

        if (requestIsWriteOperation) {
            if (currentServerIsPrimary) {
                filterChain.doFilter(cachingRequest, response);
                int timestamp = LogicalClock.getAndIncrementTimestamp();

                updateLogService.saveToLog(cachingRequest, timestamp);
                
                // only forward non-catch up messages
                if (!requestIsCatchUpReplay) {
                    logger.info("Broadcasting request with timestamp {}", timestamp);
                    forwardToBackups(cachingRequest, timestamp);
                } else {
                    logger.info("Received catch up request with timestamp {}", timestamp);
                }

            } else {
                // Check if asynchronous processing has already been started.
                AsyncContext asyncContext;
                if (!cachingRequest.isAsyncStarted()) {
                    asyncContext = cachingRequest.startAsync(cachingRequest, response);
                } else {
                    asyncContext = cachingRequest.getAsyncContext();
                }

                int timestamp = Integer.parseInt(cachingRequest.getHeader("Update-Timestamp"));
                UpdateMessage updateMessage = new UpdateMessage(timestamp, asyncContext, cachingRequest);

                logger.info("Received request with timestamp {}, enqueuing", timestamp);
                updateQueue.enqueue(updateMessage);
            }
        } else {
            filterChain.doFilter(cachingRequest, response);
        }
    }

    public void forwardToBackups(ContentCachingRequestWrapper request, int timestamp) {
        // Capture method and path
        String method = request.getMethod();
        String apiPath = request.getRequestURI();

        // Add the jwt and timestamp header
        HttpHeaders headers = new HttpHeaders();
        String jwt = request.getHeader("Authorization");
        if (jwt != null) {
            headers.add("Authorization", jwt);
        }
        headers.add("Update-Timestamp", String.valueOf(timestamp));

        // Extract the request body.
        String body = request.getContentAsString();

        Flux.fromArray(backupUrls)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(backupUrl -> {
                    String requestUrl = backupUrl.trim() + apiPath;
                    System.out.println("Sending replication request to: " + requestUrl);
                    return webClient.method(HttpMethod.valueOf(method))
                            .uri(requestUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .headers(httpHeaders -> httpHeaders.addAll(headers))
                            .body(BodyInserters.fromValue(body))
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMap(ack -> {
                                if (ack != null && !ack.isEmpty()) {
                                    logger.info("Received ACK from {}", requestUrl);
                                }
                                return Mono.just(ack);
                            })
                            .onErrorResume(err -> {
                                if (err.getMessage() != null && err.getMessage().contains("Connection refused")) {
                                    logger.warn("Connection refused error for {}", requestUrl);
                                    return Mono.empty();
                                }
                                logger.error("Replication error for {}: {}", requestUrl, err.getMessage());
                                return Mono.error(err);
                            });
                })
                .sequential()
                .collectList()
                .block(); // Wait until all backup requests complete
    }

    public boolean isWriteOperation(ContentCachingRequestWrapper request) {
        // Ensure it is not a GET request
        String requestMethod = request.getMethod();
        boolean isWriteRequest = !requestMethod.equals("GET");

        // Ensure it is not a request to election endpoints /health, /election, /leader
        // or the /login endpoint.
        String requestUri = request.getRequestURI();

        // Ensure it is an api call (not to swagger docs or h2-console)
        boolean isApiCall = requestUri.contains("api");

        boolean isDatabaseOperation = !requestUri.contains("health") &&
                !requestUri.contains("election") &&
                !requestUri.contains("leader") &&
                !requestUri.contains("login") &&
                !requestUri.contains("catchup");

        // Ensure all are true
        return isWriteRequest && isApiCall && isDatabaseOperation;
    }
}