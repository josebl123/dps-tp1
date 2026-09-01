package edu.itba.class1.exchange.client.currencyapi;

import edu.itba.class1.exchange.model.CurrencyRate;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;

public interface CurrencyApiClient {
    Collection<CurrencyRate> getRates(Currency from, Collection<Currency> to);
    Collection<CurrencyRate> getHistoricalRates(Currency from, Collection<Currency> to, LocalDate date);
    Collection<Currency> getAvailableCurrencies();
}
