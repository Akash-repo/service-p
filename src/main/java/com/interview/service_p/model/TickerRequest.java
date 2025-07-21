package com.interview.service_p.model;

import java.util.List;

public class TickerRequest {

    private List<String> tickers;


    // --- Getters ---
    public List<String> getTickers() {
        return tickers;
    }

    // --- Setters ---
    public void setSymbols(List<String> tickers) {
        this.tickers = tickers;
    }

    // --- toString() for logging/debugging ---
    @Override
    public String toString() {
        return "TickerRequest{" +
                "tickers=" + tickers +
                '}';
    }

}
