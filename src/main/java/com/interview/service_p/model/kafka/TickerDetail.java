// src/main/java/com/interview/service_p/model/TickerDetail.java
package com.interview.service_p.model.kafka;

import java.util.List;
import java.util.Map; // Import Map for the new type

// Removed @Data and other Lombok annotations
// import lombok.Data;

import java.util.List;
import java.util.Objects;

public class TickerDetail {

    private List<TickerQuery> tickers; // Changed to List<TickerQuery>
    private String email;

    public TickerDetail() {
        // Default constructor for Jackson deserialization
    }

    public TickerDetail(List<TickerQuery> tickers, String email) {
        this.tickers = tickers;
        this.email = email;
    }

    public List<TickerQuery> getTickers() {
        return tickers;
    }

    public void setTickers(List<TickerQuery> tickers) {
        this.tickers = tickers;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "TickerDetail{" +
                "tickers=" + tickers +
                ", email='" + email + '\'' +
                '}';
    }
}

