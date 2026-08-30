package edu.itba.class1.exchange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.itba.class1.exchange.http.HttpResponse;
import edu.itba.class1.exchange.parser.GsonJsonParser;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class CurrencyApiProviderTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency JPY = Currency.getInstance("JPY");

    private final CurrencyApiClient client = mock(CurrencyApiClient.class);
    private final CurrencyApiProvider provider = new CurrencyApiProvider(client, new GsonJsonParser(new Gson()));

    @Test
    void mapsMultipleRatesFromApiResponse() {
        when(client.getMultipleCurrencyRates(USD, List.of(EUR, JPY)))
                .thenReturn(ok("{\"data\":{\"EUR\":{\"code\":\"EUR\",\"value\":0.92},\"JPY\":{\"code\":\"JPY\",\"value\":145.5}}}"));

        var rates = provider.getMultipleCurrencyRates(USD, List.of(EUR, JPY));

        assertThat(rates).extracting(CurrencyRate::fromCurrency).containsExactly(USD, USD);
        assertThat(rates).extracting(CurrencyRate::toCurrency).containsExactly(EUR, JPY);
        assertThat(rates).extracting(CurrencyRate::rate).containsExactly(0.92, 145.5);
    }

    @Test
    void getsOneRateUsingTheMultipleRatesContract() {
        when(client.getMultipleCurrencyRates(USD, List.of(EUR)))
                .thenReturn(ok("{\"data\":{\"EUR\":{\"code\":\"EUR\",\"value\":0.92}}}"));

        var rate = provider.getCurrencyRate(USD, EUR);

        assertThat(rate.toCurrency()).isEqualTo(EUR);
        assertThat(rate.rate()).isEqualTo(0.92);
        verify(client).getMultipleCurrencyRates(USD, List.of(EUR));
    }

    @Test
    void returnsAnEmptyCollectionWhenNoCurrenciesAreRequested() {
        when(client.getMultipleCurrencyRates(USD, List.of())).thenReturn(ok("{\"data\":{}}"));

        assertThat(provider.getMultipleCurrencyRates(USD, List.of())).isEmpty();
    }

    @Test
    void reportsWhenTheApiDoesNotReturnARequestedRate() {
        when(client.getMultipleCurrencyRates(USD, List.of(EUR)))
                .thenReturn(ok("{\"data\":{}}"));

        assertThatThrownBy(() -> provider.getMultipleCurrencyRates(USD, List.of(EUR)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Missing exchange rate for currency: EUR");
    }

    @Test
    void mapsHistoricalRates() {
        var date = LocalDate.of(2024, 11, 20);
        when(client.getHistoricalMultipleCurrencyRates(USD, List.of(EUR), date))
                .thenReturn(ok("{\"data\":{\"EUR\":{\"code\":\"EUR\",\"value\":0.91}}}"));

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
                .thenReturn(ok("{\"data\":{\"USD\":{\"code\":\"USD\"},\"EUR\":{\"code\":\"EUR\"}}}"));

        assertThat(provider.getAvailableCurrencies())
                .containsExactly(Currency.getInstance("USD"), Currency.getInstance("EUR"));
    }

    private static HttpResponse ok(String body) {
        return new HttpResponse(body, 200);
    }
}
