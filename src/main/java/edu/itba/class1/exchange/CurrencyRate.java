package edu.itba.class1.exchange;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

public record CurrencyRate(Currency from, Currency to, BigDecimal value, Instant retrievedAt) { }