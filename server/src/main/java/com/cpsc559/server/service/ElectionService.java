package com.cpsc559.server.service;

import com.cpsc559.server.message.BullyMessage;
import com.cpsc559.server.message.ElectionMessage;
import com.cpsc559.server.message.LeaderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

// Main implementation of the bully election algorithm.
@Service
public class ElectionService {

    private static final Logger logger = LoggerFactory.getLogger(ElectionService.class);

    private final WebClient webClient;

    @Value("${server.url}")
    private String serverUrl;

    @Value("#{'${server.urls}'.split(',')}")
    private List<String> otherServerUrls;

    @Value("${server.url}")
    private String serverUrl;

    // State flags used in the election
    // The 'volatile' keyword ensures updates from one thread, are immediately visible to all other threads.
    private volatile boolean running = false;
    private volatile String leaderUrl = null;

    public ElectionService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Our implementation of the Initiate_Election(int i) pseudocode from class
    public void initiateElection() {
        logger.info("Initiating election.");
        running = true;

        // rest of implementation goes here...
    }

    // Case: received message is leader message
    public void onLeaderMessage(LeaderMessage message) {
        String newLeaderUrl = message.getLeaderUrl();

        // rest of implementation goes here...
    }

    // Case: received message is election message
    public void onElectionMessage(ElectionMessage message) {
        String senderUrl = message.getSenderUrl();

        // rest of implementation goes here...
	
        if (senderUrl.compareTo(serverUrl) < 0) {
            sendBullyMessage(serverUrl);
        }

        if (!running) {
            initiateElection(serverUrl);
        }
            
	}

    private void sendLeaderMessage(LeaderMessage message) {

        // used to broadcast message to all other servers
    }

    private void sendElectionMessage(ElectionMessage message) {

        // used to broadcast message to all other servers with greater URLs/ID [use String.CompareTo()]
    }

    private void sendBullyMessage(BullyMessage message) {

        // used to respond with a bully message

    }

    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.SECONDS)
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
                    .doOnError(e -> {
                        // Server didn't respond in time - initiate the election process
                        logger.info("Health check failed.");
                        if (!running) {
                            initiateElection();
                        }
                    })
                    .subscribe();
        }
    }

    // True if the current server is the leader
    private boolean isLeader() {
        return serverUrl.equals(leaderUrl);
    }
}
