package com.cpsc559.server.message;

import jakarta.servlet.AsyncContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.util.ContentCachingRequestWrapper;


@Getter
@Setter
@AllArgsConstructor
public class UpdateMessage implements Comparable<UpdateMessage> {
    private int timestamp;
    private AsyncContext asyncContext;
    private final ContentCachingRequestWrapper requestWrapper;

    @Override
    public int compareTo(UpdateMessage updateMessage) {
        return Integer.compare(timestamp, updateMessage.timestamp);
    }
}
