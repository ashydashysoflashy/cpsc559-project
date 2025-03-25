package com.cpsc559.server.service;

import com.cpsc559.server.message.BullyMessage;
import com.cpsc559.server.message.ElectionMessage;
import com.cpsc559.server.message.LeaderMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

// Main implementation of the bully election algorithm.
@Service
public class ElectionService {

    @Value("#{'${server.urls}'.split(',')}")
    private List<String> otherServerUrls;

    @Value("${server.url}")
    private String serverUrl;

    // State flags used in the election
    // The 'volatile' keyword ensures updates from one thread, are immediately visible to all other threads.
    private volatile boolean running = false;
    private volatile String leaderUrl = null;

    // Our implementation of the Initiate_Election(int i) pseudocode from class
    public void initiateElection(String url) {
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
}
