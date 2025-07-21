package com.interview.service_p.controller;

import com.interview.service_p.model.kafka.TickerDetail;
import com.interview.service_p.model.TickerRequest;
import com.interview.service_p.model.TickerResponse;
import com.interview.service_p.service.TickerService;

// import lombok.AllArgsConstructor;

import org.slf4j.Logger; // Needed for manual logger
import org.slf4j.LoggerFactory; // Needed for manual logger

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; // IMPORTANT: Ensure this import is present
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
// Removed @AllArgsConstructor
@RequestMapping("/v1") // Ensure this is present
public class TickerController {

    // Manual logger declaration. If @Slf4j works reliably after this, you can remove this manual line.
    // Given your previous issues, keeping this manual declaration is often safer.
    private static final Logger log = LoggerFactory.getLogger(TickerController.class);

    private final TickerService tickerService;

    // MANUAL CONSTRUCTOR FOR DEPENDENCY INJECTION
    // This explicitly initializes all 'final' fields.
    public TickerController(TickerService tickerService) {
        this.tickerService = tickerService;
    }

    @PostMapping("/ticker-analysis-sync")
    public ResponseEntity<String> tickerAnalysisSync(@RequestBody TickerDetail tickerDetail){ // Renamed method for clarity


        log.info("Initiating ticker analysis (synchronous) with ticker details: {}", tickerDetail); // Re-enabled log
        try {
            tickerService.initiateTickerAnalysis(tickerDetail);
            return ResponseEntity.ok("Ticker analysis initiated successfully.");
        } catch (Exception e) {
            log.error("Error initiating ticker analysis (synchronous): {}", e.getMessage(), e); // Re-enabled log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to initiate ticker analysis (synchronous): " + e.getMessage());
        }
    }

    @PostMapping("/ticker-analysis-async")
    public ResponseEntity<String> tickerAnalysisAsync(@RequestBody TickerDetail tickerDetail){

        log.info("Initiating ticker analysis (asynchronous) with ticker details: {}", tickerDetail); // Re-enabled log
        try {
            tickerService.initiateTickerAnalysisAsynchronously(tickerDetail);
            return ResponseEntity.ok("Ticker analysis initiated asynchronously. Check server logs for Kafka delivery status.");
        } catch (Exception e) {
            log.error("Error initiating ticker analysis (asynchronous): {}", e.getMessage(), e); // Re-enabled log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to initiate ticker analysis asynchronously: " + e.getMessage());
        }
    }

    @PostMapping("/ticker-statistics")
    public ResponseEntity<TickerResponse> getTickerStatistics(@RequestBody TickerRequest request) { // Changed to TickerRequest
        List<String> tickers = request.getTickers(); // Get tickers from the new request object
        log.info("Received request to fetch statistics for tickers: {}", tickers);

        if (tickers == null || tickers.isEmpty()) {
            log.warn("No tickers provided in the request for statistics.");
            //return ResponseEntity.badRequest().body(new TickerResponse(List.of())); // Corrected return type
            return null;
        }

        try {
            // Corrected: Call TickerService to fetch statistics
            TickerResponse tickerResponse = tickerService.getTickerStatistics(tickers);
            if (tickerResponse == null) {
                log.info("No statistics found for the provided tickers: {}", tickers);
                // Returning 200 OK with empty list is often preferred for "no results found"
                // rather than 404, unless the tickers themselves are invalid.
                //return ResponseEntity.ok(new TickerResponse(List.of())); // Corrected return type
                return null;
            }
            return ResponseEntity.ok(tickerResponse); // Corrected: return tickerResponse
        } catch (Exception e) {
            log.error("Error fetching stock statistics for tickers {}: {}", tickers, e.getMessage(), e);
            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new TickerResponse(List.of())); // Corrected return type
            return null;
        }
    }


}