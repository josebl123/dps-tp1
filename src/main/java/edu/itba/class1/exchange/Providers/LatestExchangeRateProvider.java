package edu.itba.class1.exchange.Providers;

import edu.itba.class1.exchange.CurrencyRate;

import java.util.Currency;
import java.util.Map;
import java.util.Set;

public interface LatestExchangeRateProvider {
    Map<Currency, CurrencyRate> getLatestRates(Currency base, Set<Currency> targets);
}
