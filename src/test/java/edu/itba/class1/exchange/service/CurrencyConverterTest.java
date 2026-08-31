package edu.itba.class1.exchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.itba.class1.exchange.model.Conversion;
import edu.itba.class1.exchange.model.CurrencyRate;
import edu.itba.class1.exchange.model.MoneyAmount;

class CurrencyConverterTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency JPY = Currency.getInstance("JPY");
    private static final Instant TIMESTAMP = Instant.parse("2026-08-29T12:00:00Z");
    private static final LocalDate DATE = LocalDate.of(2024, 11, 20);

    private final CurrencyRateProvider provider = mock(CurrencyRateProvider.class);
    private final CurrencyConverter converter = new CurrencyConverter(provider);

    @Test
    void convertsOneAmountAndReturnsTheRateUsed() {
        var rate = new CurrencyRate(USD, EUR, new BigDecimal("0.92"), TIMESTAMP);
        when(provider.getCurrencyRate(USD, EUR)).thenReturn(rate);

        var result = converter.convert(new MoneyAmount(USD, new BigDecimal("100")), EUR);

        assertThat(result.converted()).isEqualTo(new MoneyAmount(EUR, new BigDecimal("92")));
        assertThat(result.rateUsed()).isEqualTo(rate);
    }

    @Test
    void convertsOneAmountToSeveralCurrenciesAndReturnsEachRate() {
        var eurRate = new CurrencyRate(USD, EUR, new BigDecimal("0.92"), TIMESTAMP);
        var jpyRate = new CurrencyRate(USD, JPY, new BigDecimal("145.50"), TIMESTAMP);
        when(provider.getMultipleCurrencyRates(USD, List.of(EUR, JPY)))
                .thenReturn(List.of(eurRate, jpyRate));

        var results = converter.convertMultiple(new MoneyAmount(USD, new BigDecimal("100")), List.of(EUR, JPY));

        assertThat(results).containsExactly(
                new Conversion(new MoneyAmount(EUR, new BigDecimal("92")), eurRate),
                new Conversion(new MoneyAmount(JPY, new BigDecimal("14550")), jpyRate));
    }
    @Test
    void convertsOneAmountForAPastDateAndReturnsTheRateUsed() {
        var rate = new CurrencyRate(USD, EUR, new BigDecimal("0.91"), DATE);
        when(provider.getHistoricalCurrencyRate(USD, EUR, DATE)).thenReturn(rate);

        var result = converter.historicalConvert(new MoneyAmount(USD, new BigDecimal("100")), EUR, DATE);

        assertThat(result.converted()).isEqualTo(new MoneyAmount(EUR, new BigDecimal("91")));
        assertThat(result.rateUsed()).isEqualTo(rate);
    }

    @Test
    void convertsOneAmountToSeveralCurrenciesForAPastDate() {
        var eurRate = new CurrencyRate(USD, EUR, new BigDecimal("0.91"), DATE);
        var jpyRate = new CurrencyRate(USD, JPY, new BigDecimal("154.20"), DATE);
        when(provider.getHistoricalMultipleCurrencyRates(USD, List.of(EUR, JPY), DATE))
                .thenReturn(List.of(eurRate, jpyRate));

        var results = converter.historicalMultipleConvert(
                new MoneyAmount(USD, new BigDecimal("100")), List.of(EUR, JPY), DATE);

        assertThat(results).containsExactly(
                new Conversion(new MoneyAmount(EUR, new BigDecimal("91")), eurRate),
                new Conversion(new MoneyAmount(JPY, new BigDecimal("15420")), jpyRate));
    }

    @Test
    void keepsTheRequestedDateAsTheTimestampOfEveryHistoricalConversion() {
        var rate = new CurrencyRate(USD, EUR, new BigDecimal("0.91"), DATE);
        when(provider.getHistoricalCurrencyRate(USD, EUR, DATE)).thenReturn(rate);

        var result = converter.historicalConvert(new MoneyAmount(USD, new BigDecimal("100")), EUR, DATE);

        assertThat(result.rateUsed().timestamp()).isEqualTo(Instant.parse("2024-11-20T00:00:00Z"));
    }

}
