package edu.itba.class1.exchange;



import java.util.Collection;
import java.util.Currency;

public interface CurrencyRateProvider {
	CurrencyRate getCurrencyRate(Currency from, Currency to);
	Collection<CurrencyRate> getMultipleCurrencyRates(Currency from, Collection<Currency> to);
}
