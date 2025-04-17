package com.cpsc559.server.service;

import com.cpsc559.server.model.UpdateLog;
import com.cpsc559.server.repository.UpdateLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UpdateLogService {

    private static final Logger logger = LoggerFactory.getLogger(UpdateLogService.class);

    @Autowired
    private UpdateLogRepository updateLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void saveToLog(ContentCachingRequestWrapper request, int timestamp) throws JsonProcessingException {
        Map<String, String> headers = Collections.list(request.getHeaderNames())
                .stream().collect(Collectors.toMap(h -> h, request::getHeader));
        String headersJson = objectMapper.writeValueAsString(headers);

        UpdateLog log = new UpdateLog();
        log.setTimestamp(timestamp);
        log.setMethod(request.getMethod());
        log.setPath(request.getRequestURI());
        log.setBody(request.getContentAsString());
        log.setHeadersJson(headersJson);

        String logJson = objectMapper.writeValueAsString(log);
        logger.info("Saving request to update log: {}", logJson);

        updateLogRepository.save(log);
    }
}