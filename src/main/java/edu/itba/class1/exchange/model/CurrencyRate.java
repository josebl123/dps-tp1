package edu.itba.class1.exchange.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.Objects;

public record CurrencyRate(Currency fromCurrency, Currency toCurrency, BigDecimal rate, Instant timestamp) {

    public CurrencyRate {
        Objects.requireNonNull(fromCurrency, "fromCurrency");
        Objects.requireNonNull(toCurrency, "toCurrency");
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(timestamp, "timestamp");
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Rate must be greater than zero, got: " + rate);
        }
    }

    public CurrencyRate(Currency fromCurrency, Currency toCurrency, BigDecimal rate) {
        this(fromCurrency, toCurrency, rate, Instant.now());
    }

    public CurrencyRate(Currency fromCurrency, Currency toCurrency, BigDecimal rate, LocalDate date) {
        this(fromCurrency, toCurrency, rate, date.atStartOfDay(ZoneOffset.UTC).toInstant());
    }
}
