package edu.itba.class1.exchange.Providers;



import edu.itba.class1.exchange.CurrencyRate;

import java.util.Currency;
import java.util.List;

public interface CurrencyRateProvider {
	CurrencyRate getCurrencyRate(Currency from, List<Currency> toCurrencies);
}
