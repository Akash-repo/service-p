// src/main/java/com/interview/service_p/service/RedisTickerCacheService.java
package com.interview.service_p.service;

import com.interview.service_p.model.TickerStatistic; // Updated import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisTickerCacheService { // Renamed class

    private static final Logger log = LoggerFactory.getLogger(RedisTickerCacheService.class); // Updated logger name
    private final RedisTemplate<String, TickerStatistic> redisTemplate; // Updated generic type

    public RedisTickerCacheService(RedisTemplate<String, TickerStatistic> redisTemplate) { // Updated generic type
        this.redisTemplate = redisTemplate;
    }

    /**
     * Retrieves a TickerStatistic from Redis cache.
     * @param symbol The ticker symbol.
     * @return An Optional containing the TickerStatistic if found, empty otherwise.
     */
    public Optional<TickerStatistic> get(String symbol) { // Updated method name and return type
        try {
            TickerStatistic statistic = redisTemplate.opsForValue().get(symbol); // Updated type
            if (statistic != null) {
                log.debug("Cache hit for symbol: {}", symbol);
                return Optional.of(statistic);
            } else {
                log.debug("Cache miss for symbol: {}", symbol);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error retrieving from Redis for symbol {}: {}", symbol, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Stores a TickerStatistic in Redis cache with a specified TTL.
     * @param symbol The ticker symbol.
     * @param statistic The TickerStatistic object to store.
     * @param ttlSeconds The time-to-live for the cache entry in seconds.
     */
    public void put(String symbol, TickerStatistic statistic, long ttlSeconds) { // Updated parameter type
        if (statistic == null) {
            log.warn("Attempted to cache null statistic for symbol: {}", symbol);
            return;
        }
        try {
            redisTemplate.opsForValue().set(symbol, statistic, Duration.ofSeconds(ttlSeconds));
            log.info("Cached symbol {} with TTL of {} seconds.", symbol, ttlSeconds);
        } catch (Exception e) {
            log.error("Error putting to Redis for symbol {}: {}", symbol, e.getMessage(), e);
        }
    }
}
