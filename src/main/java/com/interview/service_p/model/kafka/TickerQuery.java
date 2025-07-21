package com.interview.service_p.model.kafka;

import java.util.Objects;

public record TickerQuery(String ticker, String query) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TickerQuery that = (TickerQuery) o;
        return Objects.equals(ticker, that.ticker) &&
                Objects.equals(query, that.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, query);
    }

    @Override
    public String toString() {
        return "TickerQuery{" +
                "ticker='" + ticker + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}