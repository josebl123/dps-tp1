package edu.itba.class1.exchange.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;

import org.junit.jupiter.api.Test;

class CurrencyRateTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Instant TIMESTAMP = Instant.parse("2026-08-29T12:00:00Z");

    @Test
    void rejectsANullRate() {
        assertThatThrownBy(() -> new CurrencyRate(USD, EUR, null, TIMESTAMP))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void rejectsAZeroRate() {
        assertThatThrownBy(() -> new CurrencyRate(USD, EUR, BigDecimal.ZERO, TIMESTAMP))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsANegativeRate() {
        assertThatThrownBy(() -> new CurrencyRate(USD, EUR, new BigDecimal("-0.92"), TIMESTAMP))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void stampsHistoricalRatesWithTheRequestedDate() {
        var rate = new CurrencyRate(USD, EUR, new BigDecimal("0.91"), LocalDate.of(2024, 11, 20));

        assertThat(rate.timestamp()).isEqualTo(Instant.parse("2024-11-20T00:00:00Z"));
    }

    @Test
    void stampsLatestRatesWithTheCurrentInstant() {
        var before = Instant.now();

        var rate = new CurrencyRate(USD, EUR, new BigDecimal("0.92"));

        assertThat(rate.timestamp()).isBetween(before, Instant.now());
    }
}
