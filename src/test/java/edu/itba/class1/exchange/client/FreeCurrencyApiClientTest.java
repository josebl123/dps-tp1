package edu.itba.class1.exchange.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.http.HttpResponse;
import edu.itba.class1.exchange.parser.GsonJsonParser;
import edu.itba.class1.exchange.error.CurrencyApiException;

import java.net.URI;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class FreeCurrencyApiClientTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency JPY = Currency.getInstance("JPY");

    private final HttpClient httpClient = mock(HttpClient.class);
    private final FreeCurrencyApiClient client = new FreeCurrencyApiClient(httpClient, new GsonJsonParser());

    @BeforeEach
    void returnAnEmptyResponse() {
        when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse("{}", 200));
    }

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

    @Test
    void failsWhenTheApiRespondsWithAnError() {
        when(httpClient.get(any(), any(), any()))
                .thenReturn(new HttpResponse("{\"message\":\"Not Found\"}", 404));

        assertThatThrownBy(() -> client.getCurrencyRate(USD, EUR))
                .isInstanceOf(CurrencyApiException.class)
                .hasMessageContaining("404")
                .hasMessageContaining("Not Found");
    }

    @Test
    void exposesTheStatusCodeOfTheFailedRequest() {
        when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse("{}", 500));

        var thrown = assertThrows(CurrencyApiException.class, client::getAvailableCurrencies);

        assertThat(thrown.statusCode()).isEqualTo(500);
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
