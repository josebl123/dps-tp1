package edu.itba.class1.exchange.service;

import edu.itba.class1.exchange.model.CurrencyRate;

import java.util.Collection;
import java.util.Currency;

public interface CurrencyRateProvider {
	CurrencyRate getCurrencyRate(Currency from, Currency to);
	Collection<CurrencyRate> getMultipleCurrencyRates(Currency from, Collection<Currency> to);
}
