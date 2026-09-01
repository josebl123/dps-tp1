package edu.itba.class1.exchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.itba.class1.exchange.client.currencyapi.CurrencyApiClient;
import edu.itba.class1.exchange.client.currencyapi.CurrencyApiProvider;
import edu.itba.class1.exchange.model.CurrencyRate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Test;

class CurrencyApiProviderTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency JPY = Currency.getInstance("JPY");

    private final CurrencyApiClient client = mock(CurrencyApiClient.class);
    private final CurrencyApiProvider provider = new CurrencyApiProvider(client);

    @Test
    void delegatesMultipleRatesToClient() {
        var expected = List.of(rate(USD, EUR, "0.92"), rate(USD, JPY, "145.5"));
        when(client.getRates(USD, List.of(EUR, JPY))).thenReturn(expected);

        var rates = provider.getMultipleCurrencyRates(USD, List.of(EUR, JPY));

        assertThat(rates).containsExactlyElementsOf(expected);
    }

    @Test
    void getsOneRateUsingTheMultipleRatesContract() {
        when(client.getRates(USD, List.of(EUR))).thenReturn(List.of(rate(USD, EUR, "0.92")));

        var rate = provider.getCurrencyRate(USD, EUR);

        assertThat(rate.toCurrency()).isEqualTo(EUR);
        assertThat(rate.rate()).isEqualByComparingTo("0.92");
        verify(client).getRates(USD, List.of(EUR));
    }

    @Test
    void returnsAnEmptyCollectionWhenNoCurrenciesAreRequested() {
        when(client.getRates(USD, List.of())).thenReturn(List.of());

        assertThat(provider.getMultipleCurrencyRates(USD, List.of())).isEmpty();
    }

    @Test
    void delegatesHistoricalRatesToClient() {
        var date = LocalDate.of(2024, 11, 20);
        var expected = List.of(new CurrencyRate(USD, EUR, new BigDecimal("0.91"), date));
        when(client.getHistoricalRates(USD, List.of(EUR), date)).thenReturn(expected);

        var rates = provider.getHistoricalMultipleCurrencyRates(USD, List.of(EUR), date);

        assertThat(rates).containsExactlyElementsOf(expected);
    }

    @Test
    void getsOneHistoricalRateUsingTheMultipleRatesContract() {
        var date = LocalDate.of(2024, 11, 20);
        when(client.getHistoricalRates(USD, List.of(EUR), date))
                .thenReturn(List.of(new CurrencyRate(USD, EUR, new BigDecimal("0.91"), date)));

        var rate = provider.getHistoricalCurrencyRate(USD, EUR, date);

        assertThat(rate.toCurrency()).isEqualTo(EUR);
        assertThat(rate.rate()).isEqualByComparingTo("0.91");
        assertThat(rate.timestamp()).isEqualTo(Instant.parse("2024-11-20T00:00:00Z"));
        verify(client).getHistoricalRates(USD, List.of(EUR), date);
    }

    @Test
    void mapsAvailableCurrencies() {
        when(client.getAvailableCurrencies()).thenReturn(List.of(USD, EUR));

        assertThat(provider.getAvailableCurrencies())
                .containsExactly(Currency.getInstance("USD"), Currency.getInstance("EUR"));
    }

    private static CurrencyRate rate(Currency from, Currency to, String value) {
        return new CurrencyRate(from, to, new BigDecimal(value));
    }
}
