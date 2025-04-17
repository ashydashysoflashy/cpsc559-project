package com.cpsc559.server.sync;

import com.cpsc559.server.repository.UpdateLogRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogicalClock {
    private final UpdateLogRepository logRepo;
    private static int timestamp;
    private static final Logger logger = LoggerFactory.getLogger(LogicalClock.class);

    @Autowired
    public LogicalClock(UpdateLogRepository logRepo) {
        this.logRepo = logRepo;
    }

    // Load the last‑seen timestamp from the DB on startup.
    @PostConstruct
    private void init() {
        Integer saved = logRepo.findMaxTimestamp();
        timestamp = (saved != null ? saved : 0);

        logger.info("Logical clock initialized with timestamp : {}", timestamp);
    }

    public static synchronized int getTimestamp() {
        return timestamp;
    }

    public static synchronized void incrementTimestamp() {
        timestamp++;
        logger.info("Logical clock incremented to : {}", timestamp);
    }

    public static synchronized int getAndIncrementTimestamp() {
        incrementTimestamp();
        return timestamp;
    }
}
