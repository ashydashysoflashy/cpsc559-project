package com.cpsc559.server.service;

import com.cpsc559.server.message.UpdateMessage;
import com.cpsc559.server.sync.LogicalClock;
import com.cpsc559.server.sync.UpdateQueue;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;


@Service
public class ApplyUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(ApplyUpdateService.class);

    @Autowired
    private UpdateQueue updateQueue;

    @Autowired
    UpdateLogService updateLogService;

    @PostConstruct
    public void startUpdateProcessor() {
        Thread processorThread = new Thread(() -> {
            logger.info("Apply-update thread started with timestamp {}", LogicalClock.getTimestamp());

            while (true) {
                try {
                    // Inspect the head of the queue
                    UpdateMessage updateMessage = updateQueue.peek();

                    if (updateMessage != null) {
                        int messageTimestamp = updateMessage.getTimestamp();
                        int nextInSequenceTimestamp = LogicalClock.getTimestamp() + 1;

                        // Check if it is safe to apply the message
                        if (messageTimestamp == nextInSequenceTimestamp) {
                            updateQueue.dequeue();
                            applyUpdate(updateMessage);
                            LogicalClock.incrementTimestamp();
                        } else {
                            // If the head is not the expected message, wait a bit before trying again.
                            Thread.sleep(50);
                        }
                    } else {
                        // Queue is empty, wait a bit before checking again.
                        Thread.sleep(50);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Apply-update thread was interrupted.", e);
                    break;
                } catch (Exception e) {
                    logger.error("Exception in apply-update thread", e);
                }
            }
        });
        processorThread.setDaemon(true);
        processorThread.start();
    }

    private void applyUpdate(UpdateMessage updateMessage) {
        AsyncContext asyncContext = updateMessage.getAsyncContext();
        HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
        ContentCachingRequestWrapper wrapper = updateMessage.getRequestWrapper();

        String targetUri = request.getRequestURI();

        logger.info("Applying update with timestamp {}. Forwarding request to URI: {}", updateMessage.getTimestamp(), targetUri);

        // Attach a listener to log when request completes
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) {
                try {
                    updateLogService.saveToLog(wrapper, updateMessage.getTimestamp());
                } catch (Exception e) {
                    logger.error("Failed to log update {}", updateMessage.getTimestamp(), e);
                }
            }

            @Override
            public void onTimeout(AsyncEvent event) {
                logger.error("Async processing timeout for update with timestamp {} at URI: {}", updateMessage.getTimestamp(), targetUri);
            }

            @Override
            public void onError(AsyncEvent event) {
                logger.error("Async processing error for update with timestamp {} at URI: {}", updateMessage.getTimestamp(), targetUri);
            }

            @Override
            public void onStartAsync(AsyncEvent event) {
                // No logging needed here
            }
        });

        try {
            // dispatch the request as normal (send to the appropriate controller method)
            asyncContext.dispatch();
        } catch (IllegalStateException e) {
            logger.error("Error forwarding update with timestamp {} to URI: {}. Exception: {}",
                    updateMessage.getTimestamp(), targetUri, e.getMessage(), e);
        }
    }
}
