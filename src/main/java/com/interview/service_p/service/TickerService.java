// src/main/java/com/interview/service_p/service/TickerService.java
package com.interview.service_p.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.service_p.config.KafkaProducerProperties;
import com.interview.service_p.config.RestConfigProperties;
import com.interview.service_p.config.RestConfigProperties.ApiProviderProperties;
import com.interview.service_p.entity.TickerStatisticEntity;
import com.interview.service_p.model.kafka.TickerDetail;
import com.interview.service_p.model.TickerResponse;
import com.interview.service_p.model.TickerStatistic;
import com.interview.service_p.model.fmp.FmpTickerQuoteResponse;
import com.interview.service_p.model.kafka.TickerDetailPayload;
import com.interview.service_p.service.mapper.TickerStatisticMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class TickerService {
    private static final Logger log = LoggerFactory.getLogger(TickerService.class);

    private final UniqueIdGenService uniqueIdGenService;
    // KafkaTemplate now uses TickerDetailPayload as its value type
    private final KafkaTemplate<String, TickerDetailPayload> kafkaTemplate;
    private final KafkaProducerProperties kafkaProducerProperties;
    private final ObjectMapper objectMapper; // Kept as it's used for other methods

    // Dependencies for Ticker Data Fetching
    private final RedisTickerCacheService redisCacheService;
    private final TickerStatisticDbService dbService;
    private final RestImpl restImpl; // Generic RestImpl
    private final TickerStatisticMapper tickerStatisticMapper;
    private final ApiProviderProperties fmpProperties;

    @Autowired
    public TickerService(UniqueIdGenService uniqueIdGenService,
                         // KafkaTemplate now uses TickerDetailPayload as its value type
                         KafkaTemplate<String, TickerDetailPayload> kafkaTemplate,
                         KafkaProducerProperties kafkaProducerProperties,
                         ObjectMapper objectMapper,
                         RedisTickerCacheService redisCacheService,
                         TickerStatisticDbService dbService,
                         RestImpl restImpl,
                         TickerStatisticMapper tickerStatisticMapper,
                         RestConfigProperties restConfigProperties) {
        this.uniqueIdGenService = uniqueIdGenService;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProducerProperties = kafkaProducerProperties;
        this.objectMapper = objectMapper;

        this.redisCacheService = redisCacheService;
        this.dbService = dbService;
        this.restImpl = restImpl;
        this.tickerStatisticMapper = tickerStatisticMapper;
        this.fmpProperties = restConfigProperties.getProviders().get("fmp");
        if (this.fmpProperties == null) {
            throw new IllegalStateException("FMP API properties (rest.api.providers.fmp) not configured!");
        }
    }

    // Synchronous Kafka message production
    public void initiateTickerAnalysis(TickerDetail tickerDetail) {
        String messageId = uniqueIdGenService.generateUniqueId();
        // Create the TickerDetailPayload object directly
        TickerDetailPayload kafkaMessagePayload = new TickerDetailPayload(
                messageId,
                tickerDetail.getTickers(), // This is now List<TickerQuery>
                tickerDetail.getEmail()
        );

        int attempt = 0;
        boolean sentSuccessfully = false;
        Exception lastException = null;

        while (attempt < kafkaProducerProperties.getMaxRetries() && !sentSuccessfully) {
            attempt++;
            log.info("Attempt {} to produce Kafka message with ID: {} to topic '{}'",
                    attempt, messageId, kafkaProducerProperties.getTopicName());

            try {
                // Send the TickerDetailPayload object directly
                CompletableFuture<SendResult<String, TickerDetailPayload>> future =
                        kafkaTemplate.send(kafkaProducerProperties.getTopicName(), messageId, kafkaMessagePayload);

                // Use get(timeout) to wait for the result with a timeout
                SendResult<String, TickerDetailPayload> result = future.get(
                        kafkaProducerProperties.getRetryDelayMs() * 2, TimeUnit.MILLISECONDS);

                log.info("Kafka message with ID: {} successfully produced to topic '{}' " +
                                "at offset {} in partition {}",
                        messageId,
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().partition());
                sentSuccessfully = true;

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                lastException = e;
                log.warn("Failed to produce Kafka message with ID: {} on attempt {}. Error: {}",
                        messageId, attempt, e.getMessage());

                if (attempt < kafkaProducerProperties.getMaxRetries()) {
                    try {
                        // Only sleep if there are more retries left
                        Thread.sleep(kafkaProducerProperties.getRetryDelayMs());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // Preserve interrupt status
                        log.error("Kafka message production retry interrupted for ID: {}", messageId);
                        break; // Exit loop if interrupted
                    }
                }
            }
        }

        if (!sentSuccessfully) {
            log.error("Failed to produce Kafka message with ID: {} after {} attempts. Last error: {}",
                    messageId, kafkaProducerProperties.getMaxRetries(), lastException != null ? lastException.getMessage() : "Unknown error");
            throw new RuntimeException("Failed to produce Kafka message for ticker analysis.", lastException);
        }
    }

    // Asynchronous Kafka message production
    public void initiateTickerAnalysisAsynchronously(TickerDetail tickerDetail) {
        String messageId = uniqueIdGenService.generateUniqueId();
        // Create the TickerDetailPayload object directly
        TickerDetailPayload kafkaMessagePayload = new TickerDetailPayload(
                messageId,
                tickerDetail.getTickers(), // This is now List<TickerQuery>
                tickerDetail.getEmail()
        );

        log.info("Attempting to asynchronously produce Kafka message with ID: {} to main topic '{}'. Request thread will be released immediately.", messageId, kafkaProducerProperties.getTopicName());

        // Note: The 'simulateProducerFailure' logic needs to be updated if it relies on a String in the list.
        // For TickerQuery, you might check for a specific ticker symbol or query string.
        boolean simulateProducerFailure = tickerDetail.getTickers() != null &&
                tickerDetail.getTickers().stream()
                        .anyMatch(tq -> "PRODUCER_FAIL".equals(tq.ticker()));


        CompletableFuture<SendResult<String, TickerDetailPayload>> future; // Updated value type

        if (simulateProducerFailure) {
            future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Simulated Kafka PRODUCER send failure for 'PRODUCER_FAIL' ticker."));
            log.warn("Simulating immediate Kafka send failure for message ID: {} due to 'PRODUCER_FAIL' ticker.", messageId);
        } else {
            // Send the TickerDetailPayload object directly
            future = kafkaTemplate.send(kafkaProducerProperties.getTopicName(), messageId, kafkaMessagePayload);
        }

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Kafka message with ID: {} successfully produced to main topic '{}' " +
                                "at offset {} in partition {} (Async Callback executed by: {})",
                        messageId,
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().partition(),
                        Thread.currentThread().getName());
            } else {
                log.error("Failed to produce Kafka message with ID: {} to main topic '{}'. Error: {} (Async Callback executed by: {}). Sending to DLQ.",
                        messageId, kafkaProducerProperties.getTopicName(), ex.getMessage(), ex, Thread.currentThread().getName());
                // Pass the TickerDetailPayload object to DLT
                sendToDeadLetterTopic(messageId, kafkaMessagePayload, ex);
            }
        });

        log.info("Kafka message send initiated for ID: {}. HTTP Request thread is now free.", messageId);
    }

    // Method to send to Dead Letter Topic
    private void sendToDeadLetterTopic(String messageId, TickerDetailPayload payload, Throwable originalException) { // Updated payload type
        log.info("Attempting to send failed producer message ID: {} to DLT topic '{}'", messageId, kafkaProducerProperties.getDltTopicName());
        // No need for objectMapper.writeValueAsString here, send the object directly
        kafkaTemplate.send(kafkaProducerProperties.getDltTopicName(), messageId, payload) // Send the object directly
                .whenComplete((dltResult, dltEx) -> {
                    if (dltEx == null) {
                        log.info("Successfully sent failed producer message ID: {} to DLT topic '{}' at offset {} in partition {}",
                                messageId, dltResult.getRecordMetadata().topic(),
                                dltResult.getRecordMetadata().offset(),
                                dltResult.getRecordMetadata().partition());
                    } else {
                        log.error("CRITICAL: Failed to send producer message ID: {} to DLT topic '{}'. This message is lost! Error: {}",
                                messageId, kafkaProducerProperties.getDltTopicName(), dltEx.getMessage(), dltEx);
                    }
                });
    }

    /**
     * Fetches ticker statistics for a list of tickers, prioritizing cache, then DB, then external API.
     * Implements Cache-Aside and Write-Through patterns.
     *
     * @param tickers A list of ticker symbols (e.g., "META", "GOOG").
     * @return A TickerResponse object containing a list of TickerStatistic objects.
     */
    public TickerResponse getTickerStatistics(List<String> tickers) {
        if (tickers == null || tickers.isEmpty()) {
            log.warn("No tickers provided to fetch ticker statistics.");
            return new TickerResponse(List.of());
        }

        log.info("Processing request for ticker statistics for tickers: {}", tickers);

        List<TickerStatistic> results = tickers.stream()
                .map(this::getStatisticForSingleTicker)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return new TickerResponse(results);
    }

    /**
     * Orchestrates fetching a single ticker statistic: Cache -> DB -> External API.
     * @param ticker The ticker symbol.
     * @return An Optional containing the TickerStatistic if found/fetched, empty otherwise.
     */
    private Optional<TickerStatistic> getStatisticForSingleTicker(String ticker) {
        // 1. Check Redis Cache
        Optional<TickerStatistic> cachedStatistic = redisCacheService.get(ticker);
        if (cachedStatistic.isPresent()) {
            log.info("Found {} in Redis cache (Cache Hit).", ticker);
            return cachedStatistic;
        }

        // 2. Cache Miss: Check Database
        Optional<TickerStatistic> dbStatisticOptional = dbService.findBySymbol(ticker);
        if (dbStatisticOptional.isPresent()) {
            TickerStatistic dbStatistic = dbStatisticOptional.get();
            Optional<TickerStatisticEntity> dbEntityOptional = dbService.findBySymbol(ticker)
                    .map(dbService::convertToEntity);


            if (dbEntityOptional.isPresent()) {
                LocalDateTime lastFetched = dbEntityOptional.get().getLastFetchedTime();
                long minutesSinceLastFetch = ChronoUnit.MINUTES.between(lastFetched, LocalDateTime.now());

                if (minutesSinceLastFetch < fmpProperties.getDbStalenessThresholdMinutes()) {
                    log.info("Found {} in DB (DB Hit) and it's fresh ({} mins old). Caching to Redis and returning.",
                            ticker, minutesSinceLastFetch);
                    redisCacheService.put(ticker, dbStatistic, fmpProperties.getDbStalenessThresholdMinutes() * 60);
                    return Optional.of(dbStatistic);
                } else {
                    log.info("Found {} in DB (DB Hit) but it's stale ({} mins old). Proceeding to external API.",
                            ticker, minutesSinceLastFetch);
                }
            } else {
                log.warn("DB hit for {} but could not retrieve entity for staleness check. Proceeding to external API.", ticker);
            }
        } else {
            log.info("Did not find {} in DB (DB Miss). Proceeding to external API.", ticker);
        }

        // 3. DB Miss or Stale: Fetch from External API
        log.info("Fetching {} from external API (FMP).", ticker);

        // Prepare resource path variables and query parameters for the generic executeGet call
        Map<String, String> resourcePathVariables = new HashMap<>();
        resourcePathVariables.put("ticker", ticker); // Replace {ticker} in resourcePath

        Map<String, String> queryParams = new HashMap<>();
        // No additional query parameters needed here, as API key is handled by RestImpl

        // Call generic RestImpl and expect FmpTickerQuoteResponse
        Optional<FmpTickerQuoteResponse> fmpResponseOptional = restImpl.executeGet(
                "fmp", // Provider name
                resourcePathVariables,
                queryParams,
                FmpTickerQuoteResponse.class // Expected response type
        );

        if (fmpResponseOptional.isPresent()) {
            // Map the FMP-specific DTO to your internal TickerStatistic model
            TickerStatistic fetched = tickerStatisticMapper.toTickerStatistic(fmpResponseOptional.get());
            dbService.save(fetched);
            redisCacheService.put(ticker, fetched, fmpProperties.getDbStalenessThresholdMinutes() * 60);
            log.info("Successfully fetched {} from FMP, saved to DB, and cached.", ticker);
            return Optional.of(fetched);
        } else {
            log.warn("Failed to fetch {} from FMP API. Data not available.", ticker);
            return Optional.empty();
        }
    }
}