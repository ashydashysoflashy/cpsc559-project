package com.cpsc559.server.service;

import com.cpsc559.server.message.BullyMessage;
import com.cpsc559.server.message.CatchUpMessage;
import com.cpsc559.server.message.ElectionMessage;
import com.cpsc559.server.message.LeaderMessage;
import com.cpsc559.server.sync.LogicalClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Main implementation of the bully election algorithm.
@Service
public class ElectionService {

    private static final Logger logger = LoggerFactory.getLogger(ElectionService.class);

    private final WebClient webClient;
    private final ExecutorService threadpool = Executors.newCachedThreadPool();

    @Value("${server.url}")
    private String serverUrl;

    @Value("${proxy.url}")
    private String proxyUrl;

    @Value("#{'${server.urls}'.split(',')}")
    private List<String> otherServerUrls;

    // State flags used in the election
    // The 'volatile' keyword ensures updates from one thread, are immediately visible to all other threads.
    private volatile boolean running = false;
    private volatile String leaderUrl;
    // Promise/signal needed to check if a leader message was received
    private volatile Sinks.One<String> leaderSignal;

    private volatile boolean catchupDone = false;


    public ElectionService(WebClient webClient) {
        this.webClient = webClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initiateElectionOnStartup() {
        if (!running) {
            initiateElection();
        }
    }

    // Initiates the election process
    public void initiateElection() {
        logger.info("Initiating election.");
        running = true;

        // Reset the sink (leader message received signal) for this election cycle.
        leaderSignal = Sinks.one();

        if (hasHighestId()) { // Automatically wins election
            leaderUrl = serverUrl;
            sendLeaderMessage(new LeaderMessage(LogicalClock.getTimestamp(), serverUrl));

        } else {
            ElectionMessage electionMessage = new ElectionMessage(serverUrl);
            sendElectionMessage(electionMessage)
                    .subscribe(bullied -> {
                        if (bullied) {
                            // Wait for a leader message signal, with a timeout.
                            leaderSignal.asMono()
                                    .timeout(Duration.ofSeconds(5))
                                    .doOnError(e -> {
                                        logger.info("No leader message received within timeout, restarting election.");
                                        initiateElection();
                                    })
                                    .subscribe(leader -> {
                                        // Leader message was received; do nothing extra here.
                                        running = false;
                                    });
                        } else {
                            leaderUrl = serverUrl;
                            sendLeaderMessage(new LeaderMessage(LogicalClock.getTimestamp(), serverUrl));
                        }
                    });
        }
    }

    private boolean hasHighestId() {
        return otherServerUrls.stream().allMatch(url -> serverUrl.compareTo(url) > 0);
    }

    // Case: received message is leader message
    public void onLeaderMessage(LeaderMessage message) {
        leaderUrl = message.getLeaderUrl();
        logger.info("Received message that {} is the current leader", leaderUrl);
        running = false;

        // Complete the signal so that waiting in initiateElection can continue.
        if (leaderSignal != null) {
            leaderSignal.tryEmitValue(leaderUrl);
        }

        int timestamp = LogicalClock.getTimestamp();
        CatchUpMessage catchUpMessage = new CatchUpMessage(timestamp, serverUrl);
        sendCatchupMessage(catchUpMessage);
    }

    // Case: received message is election message
    public BullyMessage onElectionMessage(ElectionMessage message) {
        String senderUrl = message.getSenderUrl();
        logger.info("Received election message from {}", senderUrl);

        if (senderUrl.compareTo(serverUrl) < 0) {
            logger.info("Sending bully message to {}", senderUrl);
            BullyMessage bullyMessage = new BullyMessage(serverUrl);

            if (!running) {
                // initiate election in separate thread
                logger.info("Not currently running in election");
                threadpool.submit(this::initiateElection);
            }

            return bullyMessage;
        }

        return null;

    }

    // Broadcasts leader message to all other servers & sends update to the proxy
    private void sendLeaderMessage(LeaderMessage message) {
        logger.info("Elected as leader, sending leader messages");

        // Send a leader message to all other servers at /leader
        for (String otherServerUrl : otherServerUrls) {
            logger.info("Sending leader message to {}/api/leader", otherServerUrl);
            webClient.post()
                    .uri(otherServerUrl + "/api/leader")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL)
                    .bodyValue(message)
                    .retrieve()
                    .toBodilessEntity()
                    .onErrorResume(e -> {
                        logger.error("Ignoring connection refused error for {}", otherServerUrl);
                        return Mono.empty();
                    })
                    .subscribe();
        }

        // Send /updatePrimary to the proxy
        logger.info("Updating proxy at {}/updatePrimary", proxyUrl);
        webClient.post()
                .uri(proxyUrl + "/updatePrimary")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.ALL)
                .bodyValue(message.getLeaderUrl())
                .retrieve()
                .toBodilessEntity()
                .onErrorResume(e -> {
                    logger.error("Ignoring connection refused error for {}", proxyUrl);
                    return Mono.empty();
                })
                .subscribe();
        ;

        running = false;
    }

    // Broadcast message to all other servers with greater URLs/ID
    // Returns true if bullied, false otherwise
    private Mono<Boolean> sendElectionMessage(ElectionMessage message) {
        return Flux.fromIterable(otherServerUrls)  // Convert list of URLs to a Flux stream
                .filter(otherServerUrl -> serverUrl.compareTo(otherServerUrl) < 0) // Only higher ID servers
                .flatMap(otherServerUrl -> {
                    logger.info("Sending election message to {}", otherServerUrl);

                    return webClient.post()
                            .uri(otherServerUrl + "/api/election")
                            .bodyValue(message)
                            .exchangeToMono(Mono::just)
                            .timeout(Duration.ofSeconds(5))
                            .onErrorReturn(ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build()) // Fallback on error
                            .map(response -> {
                                if (response.statusCode() == HttpStatus.OK) {
                                    logger.info("Bullied by {}", otherServerUrl);
                                    return true;
                                }
                                return false;
                            });
                }) // Stream of true & false values
                .filter(result -> result) // Filters out false
                .next() // Gets the first element of the stream
                .defaultIfEmpty(false); // If no trues (no bully messages), return false
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void heartbeat() {
        if (!isLeader()) {
            if (leaderUrl == null) {
                if (!running) {
                    initiateElection();
                }
                return;
            }

            // Sends /health request to the primary
            String uri = leaderUrl + "/api/health";
            logger.info("Sending health request to {}", uri);

            // Build the request object and send it
            webClient.get()
                    .uri(uri)
                    .accept(MediaType.ALL)
                    .retrieve()
                    .toEntity(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .onErrorResume(e -> {
                        // Server didn't respond in time - initiate the election process
                        logger.info("Health check failed. Running = {}", running);
                        if (!running) {
                            initiateElection();
                        }
                        return Mono.empty();
                    })
                    .subscribe();
        }
    }

    // True if the current server is the leader
    public boolean isLeader() {
        return serverUrl.equals(leaderUrl);
    }

    // Helper method to send the leader a request to /catchup
    private void sendCatchupMessage(CatchUpMessage message) {
        if (catchupDone || isLeader() || leaderUrl == null) return;
        logger.info("Sending catchup message to leader at {}", leaderUrl);
        webClient.post()
                .uri(leaderUrl + "/api/catchup")
                .bodyValue(message)
                .retrieve()
                .toBodilessEntity()
                .block();

        catchupDone = true;
    }
}
