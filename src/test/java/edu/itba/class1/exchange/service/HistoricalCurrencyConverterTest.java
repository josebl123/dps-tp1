package edu.itba.class1.exchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.itba.class1.exchange.model.Conversion;
import edu.itba.class1.exchange.model.CurrencyRate;
import edu.itba.class1.exchange.model.MoneyAmount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

class HistoricalCurrencyConverterTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency JPY = Currency.getInstance("JPY");
    private static final LocalDate DATE = LocalDate.of(2024, 11, 20);

    private final HistoricalCurrencyRateProvider provider = mock(HistoricalCurrencyRateProvider.class);
    private final HistoricalCurrencyConverter converter = new HistoricalCurrencyConverter(provider);

    @Test
    void convertsOneAmountForAPastDateAndReturnsTheRateUsed() {
        var rate = new CurrencyRate(USD, EUR, new BigDecimal("0.91"), DATE);
        when(provider.getHistoricalCurrencyRate(USD, EUR, DATE)).thenReturn(rate);

        var result = converter.convert(new MoneyAmount(USD, new BigDecimal("100")), EUR, DATE);

        assertThat(result.converted()).isEqualTo(new MoneyAmount(EUR, new BigDecimal("91")));
        assertThat(result.rateUsed()).isEqualTo(rate);
    }

    @Test
    void convertsOneAmountToSeveralCurrenciesForAPastDate() {
        var eurRate = new CurrencyRate(USD, EUR, new BigDecimal("0.91"), DATE);
        var jpyRate = new CurrencyRate(USD, JPY, new BigDecimal("154.20"), DATE);
        when(provider.getHistoricalMultipleCurrencyRates(USD, List.of(EUR, JPY), DATE))
                .thenReturn(List.of(eurRate, jpyRate));

        var results = converter.convertMultiple(
                new MoneyAmount(USD, new BigDecimal("100")), List.of(EUR, JPY), DATE);

        assertThat(results).containsExactly(
                new Conversion(new MoneyAmount(EUR, new BigDecimal("91")), eurRate),
                new Conversion(new MoneyAmount(JPY, new BigDecimal("15420")), jpyRate));
    }

    @Test
    void keepsTheRequestedDateAsTheTimestampOfEveryHistoricalConversion() {
        var rate = new CurrencyRate(USD, EUR, new BigDecimal("0.91"), DATE);
        when(provider.getHistoricalCurrencyRate(USD, EUR, DATE)).thenReturn(rate);

        var result = converter.convert(new MoneyAmount(USD, new BigDecimal("100")), EUR, DATE);

        assertThat(result.rateUsed().timestamp()).isEqualTo(Instant.parse("2024-11-20T00:00:00Z"));
    }
}
