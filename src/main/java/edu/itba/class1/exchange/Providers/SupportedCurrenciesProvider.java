package edu.itba.class1.exchange.Providers;

import java.util.Currency;
import java.util.List;

public interface SupportedCurrenciesProvider {
    List<Currency> getSupportedCurrencies();
}