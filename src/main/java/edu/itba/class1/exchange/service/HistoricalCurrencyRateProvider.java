package edu.itba.class1.exchange.service;

import edu.itba.class1.exchange.model.CurrencyRate;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;

public interface HistoricalCurrencyRateProvider {
	CurrencyRate getHistoricalCurrencyRate(Currency from, Currency to, LocalDate date);
	Collection<CurrencyRate> getHistoricalMultipleCurrencyRates(Currency from, Collection<Currency> to, LocalDate date);
}
