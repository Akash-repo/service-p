package com.interview.service_p.entity;

// src/main/java/com/interview/service_p/entity/TickerStatisticEntity.java

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticker_statistics") // Updated table name
public class TickerStatisticEntity { // Renamed class

    @Id
    @Column(name = "symbol", unique = true, nullable = false)
    private String symbol;

    @Column(name = "price")
    private double price;

    @Column(name = "volume") // Typo fixed here
    private double volume;

    @Column(name = "pe_ratio")
    private double peRatio;

    @Column(name = "last_updated_api")
    private String lastUpdatedApi;

    @Column(name = "last_fetched_time", nullable = false)
    private LocalDateTime lastFetchedTime;

    // --- Constructors ---
    public TickerStatisticEntity() { // Renamed constructor
    }

    public TickerStatisticEntity(String symbol, double price, double volume, double peRatio, String lastUpdatedApi, LocalDateTime lastFetchedTime) { // Renamed constructor
        this.symbol = symbol;
        this.price = price;
        this.volume = volume;
        this.peRatio = peRatio;
        this.lastUpdatedApi = lastUpdatedApi;
        this.lastFetchedTime = lastFetchedTime;
    }

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

    public String getLastUpdatedApi() {
        return lastUpdatedApi;
    }

    public LocalDateTime getLastFetchedTime() {
        return lastFetchedTime;
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

    public void setLastUpdatedApi(String lastUpdatedApi) {
        this.lastUpdatedApi = lastUpdatedApi;
    }

    public void setLastFetchedTime(LocalDateTime lastFetchedTime) {
        this.lastFetchedTime = lastFetchedTime;
    }

    @Override
    public String toString() {
        return "TickerStatisticEntity{" + // Renamed in toString
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                ", volume=" + volume +
                ", peRatio=" + peRatio +
                ", lastUpdatedApi='" + lastUpdatedApi + '\'' +
                ", lastFetchedTime=" + lastFetchedTime +
                '}';
    }
}

