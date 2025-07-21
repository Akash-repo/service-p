
// src/main/java/com/interview/service_p/service/mapper/TickerStatisticMapper.java
package com.interview.service_p.service.mapper; // New subpackage for mappers

import com.interview.service_p.model.TickerStatistic;
import com.interview.service_p.model.fmp.FmpTickerQuoteResponse; // Import the external DTO
import org.springframework.stereotype.Component;

@Component
public class TickerStatisticMapper {

    /**
     * Transforms an FMP API response DTO into our internal TickerStatistic model.
     * This is your "translateIn" method.
     * @param fmpResponse The DTO from FMP API.
     * @return The internal TickerStatistic model.
     */
    public TickerStatistic toTickerStatistic(FmpTickerQuoteResponse fmpResponse) {
        if (fmpResponse == null) {
            return null;
        }
        TickerStatistic tickerStatistic = new TickerStatistic();
        tickerStatistic.setSymbol(fmpResponse.getSymbol());
        tickerStatistic.setPrice(fmpResponse.getPrice());
        tickerStatistic.setVolume(fmpResponse.getVolume());
        tickerStatistic.setPeRatio(fmpResponse.getPe()); // Map 'pe' from FMP to 'peRatio' in internal model
        tickerStatistic.setLastUpdated(fmpResponse.getTimestamp()); // Map 'timestamp' from FMP to 'lastUpdated' in internal model
        return tickerStatistic;
    }

    // If you had an internal model that needed to be transformed for an external API request,
    // you'd have a "translateOut" method here. Example:
    /*
    public ExternalApiRequestDto toExternalApiRequest(InternalRequestDto internalRequest) {
        // ... conversion logic ...
    }
    */
}
