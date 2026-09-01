package edu.itba.class1.exchange.client.currencyapi.freecurrencyapi;

import edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response.AvailableCurrenciesResponse;
import edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response.ExchangeRateResponse;
import edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response.HistoricalExchangeRateResponse;
import edu.itba.class1.exchange.model.CurrencyRate;
import edu.itba.class1.exchange.service.error.RateNotAvailableException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;

public class FreeCurrencyResponseMapper {
    public Collection<CurrencyRate> toRates(Currency from, Collection<Currency> to, ExchangeRateResponse response) {
        return to.stream().map(target -> new CurrencyRate(from, target,
                response.findExchange(target.getCurrencyCode())
                        .orElseThrow(() -> new RateNotAvailableException(from, target))))
                .toList();
    }

    public Collection<CurrencyRate> toHistoricalRates(
            Currency from, Collection<Currency> to, LocalDate date, HistoricalExchangeRateResponse response) {
        return to.stream().map(target -> new CurrencyRate(from, target,
                response.findExchange(date, target.getCurrencyCode())
                        .orElseThrow(() -> new RateNotAvailableException(from, target, date)), date))
                .toList();
    }

    public Collection<Currency> toCurrencies(AvailableCurrenciesResponse response) {
        return response.getCurrencies();
    }
}
