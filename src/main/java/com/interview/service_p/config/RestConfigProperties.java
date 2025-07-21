// src/main/java/com/interview/service_p/config/RestConfigProperties.java
package com.interview.service_p.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rest.api") // Binds properties starting with "rest.api"
@Validated
public class RestConfigProperties {

    // This map will hold configurations for different API providers
    // Example in application.properties:
    // rest.api.providers.fmp.base-url=...
    // rest.api.providers.chatGpt.base-url=...
    private Map<String, ApiProviderProperties> providers;

    /**
     * Nested class to define properties for a single external API provider.
     * These properties will be mapped from application.properties under each provider key.
     *
     * Note: While this is a "class within a class", it's a standard and idiomatic pattern
     * for @ConfigurationProperties in Spring Boot. It helps to encapsulate related properties
     * and makes the configuration structure clear in application.properties.
     */
    public static class ApiProviderProperties {

        @NotBlank(message = "Base URL cannot be blank")
        private String baseUrl;

        @NotBlank(message = "API Key cannot be blank")
        private String apiKey;

        // Renamed for generic use: from stockQuoteEndpoint to resourcePath
        @NotBlank(message = "Resource path cannot be blank")
        private String resourcePath; // e.g., /v3/quote/{ticker} for FMP, or /v1/chat/completions for OpenAI

        @Min(value = 0, message = "Max retries must be non-negative")
        private int maxRetries;

        @Min(value = 0, message = "Retry delay (ms) must be non-negative")
        private long retryDelayMs; // Delay between retries

        @Min(value = 100, message = "Timeout (ms) must be at least 100ms") // Minimum timeout of 100ms
        private int timeoutMs; // Timeout for API calls

        @Min(value = 0, message = "DB staleness threshold (minutes) must be non-negative")
        private long dbStalenessThresholdMinutes; // How old DB data can be before considered stale and requiring API call

        // --- Getters and Setters for ApiProviderProperties ---
        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getResourcePath() { // Renamed getter
            return resourcePath;
        }

        public void setResourcePath(String resourcePath) { // Renamed setter
            this.resourcePath = resourcePath;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        public long getRetryDelayMs() {
            return retryDelayMs;
        }

        public void setRetryDelayMs(long retryDelayMs) {
            this.retryDelayMs = retryDelayMs;
        }

        public int getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        public long getDbStalenessThresholdMinutes() {
            return dbStalenessThresholdMinutes;
        }

        public void setDbStalenessThresholdMinutes(long dbStalenessThresholdMinutes) {
            this.dbStalenessThresholdMinutes = dbStalenessThresholdMinutes;
        }
    }

    // --- Getter and Setter for the 'providers' map in the main class ---
    public Map<String, ApiProviderProperties> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, ApiProviderProperties> providers) {
        this.providers = providers;
    }
}
