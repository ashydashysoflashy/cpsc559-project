package com.cpsc559.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the primary backend server properties
 */
@Configuration
@ConfigurationProperties(prefix = "backend.primary")
public class PropertiesConfig {
    // Leader server URL
    private String url;

    /**
     * @return The current leader URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Updates the primary server URL
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
