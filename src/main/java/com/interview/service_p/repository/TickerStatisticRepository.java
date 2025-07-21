// src/main/java/com/interview/service_p/repository/TickerStatisticRepository.java
package com.interview.service_p.repository; // Updated package

import com.interview.service_p.entity.TickerStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TickerStatisticRepository extends JpaRepository<TickerStatisticEntity, String> { // Updated entity type
    // Custom method to find a ticker statistic by its symbol (which is the ID)
    Optional<TickerStatisticEntity> findBySymbol(String symbol);
}
