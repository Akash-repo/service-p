
// src/main/java/com/interview/service_p/model/fmp/FmpTickerQuoteResponse.java
package com.interview.service_p.model.fmp; // New subpackage for external API DTOs

// This class represents the structure of a single ticker quote object from FMP.
// IMPORTANT: Adjust field names and types to precisely match FMP's actual JSON response.
public class FmpTickerQuoteResponse {

    private String symbol;
    private double price;
    private double volume;
    private double pe; // FMP often uses 'pe' for P/E ratio
    private String timestamp; // FMP might use 'timestamp' or similar for last updated time

    // --- Getters ---
    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public double getVolume() {
        return volume;
    }

    public double getPe() {
        return pe;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // --- Setters ---
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void setPe(double pe) {
        this.pe = pe;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "FmpTickerQuoteResponse{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                ", volume=" + volume +
                ", pe=" + pe +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
