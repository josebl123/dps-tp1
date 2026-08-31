package edu.itba.class1.exchange.service;

import java.util.Collection;
import java.util.Currency;

public interface CurrencyCatalog {
    Collection<Currency> getAvailableCurrencies();
}
