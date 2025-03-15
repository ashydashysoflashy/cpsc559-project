package com.cpsc559.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ProxyConfig {
    // The Web Client that allows us to make api calls to our servers
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
