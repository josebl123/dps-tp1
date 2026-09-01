package edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import lombok.Setter;

@Setter
public class HistoricalExchangeRateResponse {
    private Map<String, Map<String, BigDecimal>> data;

    public Optional<BigDecimal> findExchange(LocalDate date, String toCurrency) {
        return Optional.ofNullable(data)
                .map(ratesByDate -> ratesByDate.get(date.toString()))
                .map(rates -> rates.get(toCurrency));
    }
}
