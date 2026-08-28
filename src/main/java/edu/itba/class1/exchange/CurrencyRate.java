package edu.itba.class1.exchange;

import java.time.Instant;

public record CurrencyRate(double rate, Instant timestamp) {
    
    public CurrencyRate(double rate) {
        this(rate, Instant.now()); //TODO: if the api provides timestamp, use that. no clue
    }
}
