package edu.itba.class1.exchange.Providers;

import edu.itba.class1.exchange.CurrencyRate;

import java.util.Currency;

public interface LatestExchangeRateProvider {
    CurrencyRate getLatestExchangeRate(Currency from, Currency to);
}
