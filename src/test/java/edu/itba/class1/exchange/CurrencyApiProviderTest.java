package edu.itba.class1.exchange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.itba.class1.exchange.parser.GsonJsonParser;

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
    void mapsMultipleRatesFromApiResponse() {
        when(client.getMultipleCurrencyRates(USD, List.of(EUR, JPY)))
                .thenReturn(rates("{\"data\":{\"EUR\":{\"code\":\"EUR\",\"value\":0.92},\"JPY\":{\"code\":\"JPY\",\"value\":145.5}}}"));

        var rates = provider.getMultipleCurrencyRates(USD, List.of(EUR, JPY));

        assertThat(rates).extracting(CurrencyRate::fromCurrency).containsExactly(USD, USD);
        assertThat(rates).extracting(CurrencyRate::toCurrency).containsExactly(EUR, JPY);
        assertThat(rates).extracting(CurrencyRate::rate).containsExactly(0.92, 145.5);
    }

    @Test
    void getsOneRateUsingTheMultipleRatesContract() {
        when(client.getMultipleCurrencyRates(USD, List.of(EUR)))
                .thenReturn(rates("{\"data\":{\"EUR\":{\"code\":\"EUR\",\"value\":0.92}}}"));

        var rate = provider.getCurrencyRate(USD, EUR);

        assertThat(rate.toCurrency()).isEqualTo(EUR);
        assertThat(rate.rate()).isEqualTo(0.92);
        verify(client).getMultipleCurrencyRates(USD, List.of(EUR));
    }

    @Test
    void returnsAnEmptyCollectionWhenNoCurrenciesAreRequested() {
        when(client.getMultipleCurrencyRates(USD, List.of())).thenReturn(rates("{\"data\":{}}"));

        assertThat(provider.getMultipleCurrencyRates(USD, List.of())).isEmpty();
    }

    @Test
    void reportsWhenTheApiDoesNotReturnARequestedRate() {
        when(client.getMultipleCurrencyRates(USD, List.of(EUR)))
                .thenReturn(rates("{\"data\":{}}"));

        assertThatThrownBy(() -> provider.getMultipleCurrencyRates(USD, List.of(EUR)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Missing exchange rate for currency: EUR");
    }

    @Test
    void mapsHistoricalRates() {
        var date = LocalDate.of(2024, 11, 20);
        when(client.getHistoricalMultipleCurrencyRates(USD, List.of(EUR), date))
                .thenReturn(rates("{\"data\":{\"EUR\":{\"code\":\"EUR\",\"value\":0.91}}}"));

        var rates = provider.getHistoricalMultipleCurrencyRates(USD, List.of(EUR), date);

        assertThat(rates).singleElement().satisfies(rate -> {
            assertThat(rate.fromCurrency()).isEqualTo(USD);
            assertThat(rate.toCurrency()).isEqualTo(EUR);
            assertThat(rate.rate()).isEqualTo(0.91);
        });
    }

    @Test
    void mapsAvailableCurrencies() {
        when(client.getAvailableCurrencies())
                .thenReturn(currencies("{\"data\":{\"USD\":{\"code\":\"USD\"},\"EUR\":{\"code\":\"EUR\"}}}"));

        assertThat(provider.getAvailableCurrencies())
                .containsExactly(Currency.getInstance("USD"), Currency.getInstance("EUR"));
    }

    private static ExchangeRateResponse rates(String body) {
        return new GsonJsonParser().parse(body, ExchangeRateResponse.class);
    }

    private static AvailableCurrenciesResponse currencies(String body) {
        return new GsonJsonParser().parse(body, AvailableCurrenciesResponse.class);
    }
}
