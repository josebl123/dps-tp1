package edu.itba.class1.exchange.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.http.HttpResponse;
import edu.itba.class1.exchange.parser.GsonJsonParser;
import edu.itba.class1.exchange.client.error.AuthenticationFailedException;
import edu.itba.class1.exchange.client.error.CurrencyProviderException;
import edu.itba.class1.exchange.client.error.CurrencyProviderRateLimitException;
import edu.itba.class1.exchange.client.error.CurrencyProviderResourceNotFoundException;
import edu.itba.class1.exchange.client.error.InvalidProviderRequestException;

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
    private final FreeCurrencyApiClient client = new FreeCurrencyApiClient(
            httpClient, new GsonJsonParser(), ResponseStatusChecker.forCurrencyProvider());

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
    void mapsAuthenticationStatusesToAuthenticationFailure() {
        when(httpClient.get(any(), any(), any()))
                .thenReturn(new HttpResponse("{}", 401));

        assertThatThrownBy(() -> client.getCurrencyRate(USD, EUR))
                .isExactlyInstanceOf(AuthenticationFailedException.class);
    }

    @Test
    void mapsRateLimitStatusToRateLimitFailure() {
        when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse("{}", 429));

        assertThatThrownBy(client::getAvailableCurrencies)
                .isExactlyInstanceOf(CurrencyProviderRateLimitException.class);
    }

    @Test
    void mapsNotFoundStatusToResourceNotFound() {
        when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse("{}", 404));

        assertThatThrownBy(client::getAvailableCurrencies)
                .isExactlyInstanceOf(CurrencyProviderResourceNotFoundException.class);
    }

    @Test
    void mapsUnprocessableStatusToInvalidRequest() {
        when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse("{}", 422));

        assertThatThrownBy(client::getAvailableCurrencies)
                .isExactlyInstanceOf(InvalidProviderRequestException.class);
    }

    @Test
    void mapsUnknownStatusToGenericProviderFailure() {
        when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse("{}", 500));

        assertThatThrownBy(client::getAvailableCurrencies)
                .isExactlyInstanceOf(CurrencyProviderException.class);
    }

    @Test
    void doesNotParseAnErrorResponse() {
        when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse("bad", 500));

        assertThatThrownBy(client::getAvailableCurrencies)
                .isExactlyInstanceOf(CurrencyProviderException.class);
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
