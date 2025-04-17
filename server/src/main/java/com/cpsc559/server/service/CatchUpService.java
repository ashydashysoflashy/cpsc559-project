package com.cpsc559.server.service;

import com.cpsc559.server.model.UpdateLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

// Service that forwards missed requests to the backups
// It just constructs a request object using the logs, and then sends it to the backup to be handled normally
@Service
public class CatchUpService {

    private static final Logger logger = LoggerFactory.getLogger(CatchUpService.class);

    private final WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    public CatchUpService(WebClient webClient) {
        this.webClient = webClient;
    }

    public void sendToSingleBackup(String backupUrl, UpdateLog log) throws JsonProcessingException {
        Map<String, String> hdrs = objectMapper.readValue(
                log.getHeadersJson(),
                new TypeReference<Map<String, String>>() {
                }
        );

        HttpHeaders headers = new HttpHeaders();
        hdrs.forEach(headers::add);
        headers.add("Update-Timestamp", String.valueOf(log.getTimestamp()));
        headers.add("X-Catchup", "true");

        logger.info("Sending catch up request to server:{}, with timestamp {}", backupUrl, log.getTimestamp());

        webClient.method(HttpMethod.valueOf(log.getMethod()))
                .uri(backupUrl + log.getPath())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.addAll(headers))
                .body(BodyInserters.fromValue(log.getBody()))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
