package edu.itba.class1.exchange.client;

import edu.itba.class1.exchange.model.AvailableCurrenciesResponse;
import edu.itba.class1.exchange.model.ExchangeRateResponse;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;

public interface CurrencyApiClient {
    ExchangeRateResponse getCurrencyRate(Currency from, Currency to);
    ExchangeRateResponse getMultipleCurrencyRates(Currency from, Collection<Currency> to);
    AvailableCurrenciesResponse getAvailableCurrencies();
    ExchangeRateResponse getHistoricalMultipleCurrencyRates(Currency from, Collection<Currency> to, LocalDate date);
}
