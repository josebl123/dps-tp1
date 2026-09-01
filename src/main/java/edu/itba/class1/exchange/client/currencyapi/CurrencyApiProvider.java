package edu.itba.class1.exchange.client.currencyapi;

import edu.itba.class1.exchange.model.CurrencyRate;
import edu.itba.class1.exchange.service.CurrencyCatalog;
import edu.itba.class1.exchange.service.CurrencyRateProvider;
import edu.itba.class1.exchange.service.HistoricalCurrencyRateProvider;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

@AllArgsConstructor
public class CurrencyApiProvider implements CurrencyRateProvider, HistoricalCurrencyRateProvider, CurrencyCatalog {
    private final CurrencyApiClient currencyApiClient;

    @Override
    public CurrencyRate getCurrencyRate(Currency from, Currency to) {
        return this.getMultipleCurrencyRates(from, List.of(to)).stream().findFirst().orElseThrow();
    }

    @Override
    public Collection<CurrencyRate> getMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies) {
        return this.currencyApiClient.getRates(fromCurrency, toCurrencies);
    }

    @Override
    public CurrencyRate getHistoricalCurrencyRate(Currency from, Currency to, LocalDate date) {
        return this.getHistoricalMultipleCurrencyRates(from, List.of(to), date).stream().findFirst().orElseThrow();
    }

    @Override
    public Collection<CurrencyRate> getHistoricalMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies, LocalDate date) {
        return this.currencyApiClient.getHistoricalRates(fromCurrency, toCurrencies, date);
    }

    @Override
    public Collection<Currency> getAvailableCurrencies() {
        return this.currencyApiClient.getAvailableCurrencies();
    }
}
