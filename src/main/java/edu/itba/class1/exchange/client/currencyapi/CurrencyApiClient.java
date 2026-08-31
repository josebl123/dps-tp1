package edu.itba.class1.exchange.client.currencyapi;

import edu.itba.class1.exchange.client.currencyapi.response.AvailableCurrenciesResponse;
import edu.itba.class1.exchange.client.currencyapi.response.ExchangeRateResponse;
import edu.itba.class1.exchange.client.currencyapi.response.HistoricalExchangeRateResponse;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;

public interface CurrencyApiClient {
    ExchangeRateResponse getMultipleCurrencyRates(Currency from, Collection<Currency> to);
    AvailableCurrenciesResponse getAvailableCurrencies();
    HistoricalExchangeRateResponse getHistoricalMultipleCurrencyRates(Currency from, Collection<Currency> to, LocalDate date);
}
