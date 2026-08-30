package edu.itba.class1.exchange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.http.HttpResponse;

import java.net.URI;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class FreeCurrencyApiClientTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency JPY = Currency.getInstance("JPY");

    private final HttpClient httpClient = mock(HttpClient.class);
    private final FreeCurrencyApiClient client = new FreeCurrencyApiClient(httpClient);

    @Test
    void buildsSingleRateRequest() {
        client.getCurrencyRate(USD, EUR);

        assertThat(capturedUrl()).isEqualTo(URI.create("https://api.currencyapi.com/v3/latest"));
        assertThat(capturedQuery()).containsEntry("base_currency", USD)
                .containsEntry("currencies", EUR);
    }

    @Test
    void buildsLatestRateRequest() {
        client.getMultipleCurrencyRates(USD, List.of(EUR, JPY));

        verify(httpClient).get(any(), any(), any());
        assertThat(capturedUrl()).isEqualTo(URI.create("https://api.currencyapi.com/v3/latest"));
        assertThat(capturedQuery()).containsEntry("base_currency", USD)
                .containsEntry("currencies", "EUR,JPY");
    }

    @Test
    void buildsHistoricalRateRequest() {
        client.getHistoricalMultipleCurrencyRates(USD, List.of(EUR), LocalDate.of(2024, 11, 20));

        assertThat(capturedUrl()).isEqualTo(URI.create("https://api.currencyapi.com/v3/historical"));
        assertThat(capturedQuery()).containsEntry("base_currency", USD)
                .containsEntry("currencies", "EUR")
                .containsEntry("date", "2024-11-20");
    }

    @Test
    void buildsAvailableCurrenciesRequest() {
        client.getAvailableCurrencies();

        assertThat(capturedUrl()).isEqualTo(URI.create("https://api.currencyapi.com/v3/currencies"));
        assertThat(capturedQuery()).isEmpty();
    }

    private URI capturedUrl() {
        var captor = ArgumentCaptor.forClass(URI.class);
        verify(httpClient).get(captor.capture(), any(), any());
        return captor.getValue();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> capturedQuery() {
        var captor = ArgumentCaptor.forClass(Map.class);
        verify(httpClient).get(any(), captor.capture(), any());
        return captor.getValue();
    }
}
