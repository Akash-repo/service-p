// src/main/java/com/interview/service_p/service/TickerStatisticDbService.java
package com.interview.service_p.service;

import com.interview.service_p.model.TickerStatistic; // Updated import
import com.interview.service_p.entity.TickerStatisticEntity; // Updated import
import com.interview.service_p.repository.TickerStatisticRepository; // Updated import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TickerStatisticDbService { // Renamed class

    private static final Logger log = LoggerFactory.getLogger(TickerStatisticDbService.class); // Updated logger name
    private final TickerStatisticRepository tickerStatisticRepository; // Updated repository type

    public TickerStatisticDbService(TickerStatisticRepository tickerStatisticRepository) { // Updated parameter type
        this.tickerStatisticRepository = tickerStatisticRepository;
    }

    /**
     * Saves or updates a TickerStatistic in the database.
     *
     * @param statistic The TickerStatistic object to save.
     * @return The saved TickerStatistic.
     */
    @Transactional
    public TickerStatistic save(TickerStatistic statistic) { // Updated parameter and return type
        if (statistic == null || statistic.getSymbol() == null || statistic.getSymbol().isBlank()) {
            log.warn("Attempted to save a null or invalid TickerStatistic to DB.");
            return null;
        }
        try {
            // Convert TickerStatistic DTO to Entity
            TickerStatisticEntity entity = convertToEntity(statistic); // Updated entity type
            entity.setLastFetchedTime(LocalDateTime.now()); // Set current timestamp for DB record

            TickerStatisticEntity savedEntity = tickerStatisticRepository.save(entity); // Updated entity type
            log.info("Saved/Updated symbol {} in DB. Last fetched: {}", savedEntity.getSymbol(), savedEntity.getLastFetchedTime());
            return convertToDto(savedEntity); // Updated DTO type
        } catch (Exception e) {
            log.error("Error saving TickerStatistic for symbol {} to DB: {}", statistic.getSymbol(), e.getMessage(), e); // Updated log message
            return null;
        }
    }

    /**
     * Finds a TickerStatistic by its symbol in the database.
     *
     * @param symbol The ticker symbol.
     * @return An Optional containing the TickerStatistic if found, empty otherwise.
     */
    public Optional<TickerStatistic> findBySymbol(String symbol) { // Updated return type
        if (symbol == null || symbol.isBlank()) {
            log.warn("Attempted to find a null or blank symbol in DB.");
            return Optional.empty();
        }
        try {
            Optional<TickerStatisticEntity> entityOptional = tickerStatisticRepository.findBySymbol(symbol); // Updated entity type
            if (entityOptional.isPresent()) {
                log.debug("DB hit for symbol: {}", symbol);
                return Optional.of(convertToDto(entityOptional.get())); // Updated DTO type
            } else {
                log.debug("DB miss for symbol: {}", symbol);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error retrieving from DB for symbol {}: {}", symbol, e.getMessage(), e);
            return Optional.empty();
        }
    }

    // --- Helper methods for DTO-Entity conversion ---
    // Made public for use in StockDataService's staleness check
    public TickerStatisticEntity convertToEntity(TickerStatistic dto) { // Updated parameter type
        TickerStatisticEntity entity = new TickerStatisticEntity(); // Updated entity type
        entity.setSymbol(dto.getSymbol());
        entity.setPrice(dto.getPrice());
        entity.setVolume(dto.getVolume());
        entity.setPeRatio(dto.getPeRatio());
        entity.setLastUpdatedApi(dto.getLastUpdated());
        return entity;
    }

    private TickerStatistic convertToDto(TickerStatisticEntity entity) { // Updated parameter and return type
        TickerStatistic dto = new TickerStatistic(); // Updated DTO type
        dto.setSymbol(entity.getSymbol());
        dto.setPrice(entity.getPrice());
        dto.setVolume(entity.getVolume());
        dto.setPeRatio(entity.getPeRatio());
        dto.setLastUpdated(entity.getLastUpdatedApi());
        return dto;
    }
}

