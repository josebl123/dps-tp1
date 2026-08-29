package edu.itba.class1.exchange;

import java.time.Instant;
import java.util.Currency;

public record CurrencyRate(Currency fromCurrency, Currency toCurrency, double rate, Instant timestamp) {
    
    public CurrencyRate(Currency fromCurrency, Currency toCurrency,double rate) {
        this(fromCurrency,toCurrency, rate, Instant.now()); //TODO: if the api provides timestamp, use that. no clue
    }
}
