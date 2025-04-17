package com.cpsc559.server.controller;

import com.cpsc559.server.message.CatchUpMessage;
import com.cpsc559.server.model.UpdateLog;
import com.cpsc559.server.repository.UpdateLogRepository;
import com.cpsc559.server.service.CatchUpService;
import com.cpsc559.server.sync.LogicalClock;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// After a successful election, a backup will make an api call to the primary at this route
// The primary then decides if it needs to "help" the backup catch up or not
@RestController
@RequestMapping("/api")
public class CatchUpController {

    @Autowired
    private UpdateLogRepository logRepo;

    @Autowired
    private CatchUpService catchUpService;

    @PostMapping("/catchup")
    public ResponseEntity<Void> catchUp(@RequestBody CatchUpMessage message) throws JsonProcessingException {
        // if they’re already up‑to‑date, nothing to do
        if (message.since >= LogicalClock.getTimestamp()) {
            return ResponseEntity.ok().build();
        }

        // get the requests that the backup is missing
        List<UpdateLog> missing = logRepo.findByTimestampGreaterThanOrderByTimestampAsc(message.since);
        for (UpdateLog log : missing) {
            // and replay *each* to the caller server
            catchUpService.sendToSingleBackup(message.serverUrl, log);
        }
        return ResponseEntity.ok().build();
    }
}
