// src/main/java/com/interview/service_p/config/KafkaProducerProperties.java
package com.interview.service_p.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Component
@ConfigurationProperties(prefix = "kafka.producer")
@Validated
public class KafkaProducerProperties {

    @NotBlank(message = "Kafka topic name cannot be blank")
    private String topicName;

    @NotBlank(message = "Kafka DLT topic name cannot be blank")
    private String dltTopicName;

    @Min(value = 0, message = "Max retries must be non-negative")
    private int maxRetries;

    @Min(value = 0, message = "Retry delay (ms) must be non-negative")
    private long retryDelayMs;

    @NotBlank(message = "Kafka bootstrap servers cannot be blank")
    private String bootstrapServers;

    @NotBlank(message = "Kafka producer key serializer cannot be blank")
    private String keySerializer;

    @NotBlank(message = "Kafka producer value serializer cannot be blank")
    private String valueSerializer; // This will now be KafkaAvroSerializer

    @NotBlank(message = "Kafka producer acks cannot be blank")
    private String acks;

    @Min(value = 0, message = "Linger MS must be non-negative")
    private int lingerMs;

    @Min(value = 0, message = "Batch size must be non-negative")
    private int batchSize;

    @NotBlank(message = "Schema Registry URL cannot be blank") // NEW: For Avro
    private String schemaRegistryUrl;

    // Getters and Setters
    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getDltTopicName() {
        return dltTopicName;
    }

    public void setDltTopicName(String dltTopicName) {
        this.dltTopicName = dltTopicName;
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

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public int getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(int lingerMs) {
        this.lingerMs = lingerMs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getSchemaRegistryUrl() { // NEW: Getter for Schema Registry URL
        return schemaRegistryUrl;
    }

    public void setSchemaRegistryUrl(String schemaRegistryUrl) { // NEW: Setter for Schema Registry URL
        this.schemaRegistryUrl = schemaRegistryUrl;
    }
}
