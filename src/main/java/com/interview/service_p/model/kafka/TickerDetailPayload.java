package com.interview.service_p.model.kafka;

import java.util.List;
import java.util.Objects;

public record TickerDetailPayload(String messageId, List<TickerQuery> tickers, String email) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TickerDetailPayload that = (TickerDetailPayload) o;
        return Objects.equals(messageId, that.messageId) &&
                Objects.equals(tickers, that.tickers) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, tickers, email);
    }

    @Override
    public String toString() {
        return "TickerDetailPayload{" +
                "messageId='" + messageId + '\'' +
                ", tickers=" + tickers +
                ", email='" + email + '\'' +
                '}';
    }
}