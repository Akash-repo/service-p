// src/main/java/com/interview/service_p/config/KafkaProducerProperties.java
package com.interview.service_p.config;

// Removed Lombok @Data
// import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated; // Optional: for validation annotations

@Component // Make it a Spring bean
@ConfigurationProperties(prefix = "app.kafka.producer") // Binds properties starting with 'app.kafka.producer'
// Removed @Data
@Validated // Optional: Enables validation on this class
public class KafkaProducerProperties {

    private String topicName;

    private String dltTopicName;

    private int maxRetries;

    private long retryDelayMs;

    // --- Manual Getters ---
    public String getTopicName() {
        return topicName;
    }

    public String getDltTopicName() {
        return dltTopicName;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getRetryDelayMs() {
        return retryDelayMs;
    }

    // --- Manual Setters ---
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setDltTopicName(String dltTopicName) {
        this.dltTopicName = dltTopicName;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public void setRetryDelayMs(long retryDelayMs) {
        this.retryDelayMs = retryDelayMs;
    }
}
