package com.interview.service_p.model;

// src/main/java/com/interview/service_p/model/TickerStatistic.java

// No Lombok for robustness, manually define getters/setters
public class TickerStatistic { // Renamed class
    private String symbol;
    private double price;
    private double volume;
    private double peRatio; // Price-to-Earnings Ratio (example statistic)
    private String lastUpdated; // Example: timestamp of the data

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

    public double getPeRatio() {
        return peRatio;
    }

    public String getLastUpdated() {
        return lastUpdated;
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

    public void setPeRatio(double peRatio) {
        this.peRatio = peRatio;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // --- toString() for logging/debugging ---
    @Override
    public String toString() {
        return "TickerStatistic{" + // Renamed in toString
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                ", volume=" + volume +
                ", peRatio=" + peRatio +
                ", lastUpdated='" + lastUpdated + '\'' +
                '}';
    }
}

