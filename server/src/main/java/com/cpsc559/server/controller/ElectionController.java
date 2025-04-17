package com.cpsc559.server.controller;

import com.cpsc559.server.message.BullyMessage;
import com.cpsc559.server.message.ElectionMessage;
import com.cpsc559.server.message.LeaderMessage;
import com.cpsc559.server.model.UpdateLog;
import com.cpsc559.server.repository.UpdateLogRepository;
import com.cpsc559.server.service.CatchUpService;
import com.cpsc559.server.service.ElectionService;
import com.cpsc559.server.sync.LogicalClock;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    @Autowired
    private UpdateLogRepository logRepo;

    @Autowired
    private CatchUpService catchUpService;

    // Case: received message is election message
    @PostMapping("/election")
    public ResponseEntity<?> handleElection(@RequestBody ElectionMessage message) {

        BullyMessage bullyMessage = electionService.onElectionMessage(message);

        // The server has to send back a 204 but the client can wait for a 200 response using time out
        if (bullyMessage != null)
            return ResponseEntity.ok(bullyMessage);
        else
            return ResponseEntity.noContent().build();
    }

    // Case: received message is leader message
    @PostMapping("/leader")
    public void handleLeader(@RequestBody LeaderMessage message) throws JsonProcessingException {

        // If we are the current leader, and we receive a leader message
        // the new leader may need to be caught up.
        // If so, send them their missing requests.
        if (electionService.isLeader() && message.since < LogicalClock.getTimestamp()) {
            List<UpdateLog> missing = logRepo.findByTimestampGreaterThanOrderByTimestampAsc(message.since);
            for (UpdateLog log : missing) {
                // replay *each* to the caller server
                catchUpService.sendToSingleBackup(message.getLeaderUrl(), log);
            }
        }
        electionService.onLeaderMessage(message);
    }
}
