package edu.itba.class1.exchange;

import edu.itba.class1.exchange.http.HttpResponse;

import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;

public interface CurrencyApiClient {
    HttpResponse getCurrencyRate(Currency from, Currency to);
    HttpResponse getMultipleCurrencyRates(Currency from, Collection<Currency> to);
    HttpResponse getAvailableCurrencies();
}
