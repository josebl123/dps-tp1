package edu.itba.class1.exchange.client.currencyapi;

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
import edu.itba.class1.exchange.client.error.InvalidProviderResponseException;

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
    private final FreeCurrencyApiClient client = new FreeCurrencyApiClient(httpClient, new GsonJsonParser(), "test-api-key");

    @BeforeEach
    void returnAnEmptyResponse() {
        when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse("{}", 200));
    }

    @Test
    void buildsLatestRateRequest() {
        client.getMultipleCurrencyRates(USD, List.of(EUR, JPY));

        assertThat(capturedUrl()).isEqualTo(URI.create("https://api.freecurrencyapi.com/v1/latest"));
        assertThat(capturedQuery()).containsEntry("base_currency", "USD")
                .containsEntry("currencies", "EUR,JPY");
    }

    @Test
    void buildsHistoricalRateRequest() {
        client.getHistoricalMultipleCurrencyRates(USD, List.of(EUR), LocalDate.of(2024, 11, 20));

        assertThat(capturedUrl()).isEqualTo(URI.create("https://api.freecurrencyapi.com/v1/historical"));
        assertThat(capturedQuery()).containsEntry("base_currency", "USD")
                .containsEntry("currencies", "EUR")
                .containsEntry("date", "2024-11-20");
    }

    @Test
    void buildsAvailableCurrenciesRequest() {
        client.getAvailableCurrencies();

        assertThat(capturedUrl()).isEqualTo(URI.create("https://api.freecurrencyapi.com/v1/currencies"));
        assertThat(capturedQuery()).isEmpty();
    }

    @Test
    void parsesTheLatestResponseShape() {
        when(httpClient.get(any(), any(), any()))
                .thenReturn(new HttpResponse("{\"data\":{\"EUR\":0.918456}}", 200));

        var response = client.getMultipleCurrencyRates(USD, List.of(EUR));

        assertThat(response.findExchange("EUR")).isPresent();
    }

    @Test
    void parsesTheHistoricalResponseShape() {
        var date = LocalDate.of(2024, 11, 20);
        when(httpClient.get(any(), any(), any()))
                .thenReturn(new HttpResponse("{\"data\":{\"2024-11-20\":{\"EUR\":0.879908}}}", 200));

        var response = client.getHistoricalMultipleCurrencyRates(USD, List.of(EUR), date);

        assertThat(response.findExchange(date, "EUR")).isPresent();
    }

    @Test
    void mapsAuthenticationStatusesToAuthenticationFailure() {
        when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse("{}", 401));

        assertThatThrownBy(() -> client.getMultipleCurrencyRates(USD, List.of(EUR)))
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
    void reportsTheStatusAndBodyOfTheFailedRequest() {
        when(httpClient.get(any(), any(), any()))
                .thenReturn(new HttpResponse("{\"message\":\"Invalid authentication credentials\"}", 401));

        assertThatThrownBy(() -> client.getMultipleCurrencyRates(USD, List.of(EUR)))
                .isExactlyInstanceOf(AuthenticationFailedException.class)
                .hasMessageContaining("401")
                .hasMessageContaining("Invalid authentication credentials")
                .satisfies(thrown -> {
                    var exception = (CurrencyProviderException) thrown;
                    assertThat(exception.statusCode()).isEqualTo(401);
                    assertThat(exception.responseBody()).contains("Invalid authentication credentials");
                });
    }

    @Test
    void translatesParserFailuresToProviderResponseFailures() {
        when(httpClient.get(any(), any(), any())).thenReturn(new HttpResponse("{malformed", 200));

        assertThatThrownBy(client::getAvailableCurrencies)
                .isExactlyInstanceOf(InvalidProviderResponseException.class)
                .hasMessageContaining("{malformed")
                .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    void sendsTheConfiguredApiKeyOnEveryRequest() {
        client.getAvailableCurrencies();

        assertThat(capturedHeaders()).containsEntry("apikey", "test-api-key")
                .containsEntry("accept", "application/json");
    }

    private URI capturedUrl() {
        var captor = ArgumentCaptor.forClass(URI.class);
        verify(httpClient).get(captor.capture(), any(), any());
        return captor.getValue();
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> capturedHeaders() {
        var captor = ArgumentCaptor.forClass(Map.class);
        verify(httpClient).get(any(), any(), captor.capture());
        return captor.getValue();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> capturedQuery() {
        var captor = ArgumentCaptor.forClass(Map.class);
        verify(httpClient).get(any(), captor.capture(), any());
        return captor.getValue();
    }
}
