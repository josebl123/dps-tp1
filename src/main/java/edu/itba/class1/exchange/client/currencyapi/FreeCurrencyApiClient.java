package edu.itba.class1.exchange.client.currencyapi;

import edu.itba.class1.exchange.client.currencyapi.response.AvailableCurrenciesResponse;
import edu.itba.class1.exchange.client.currencyapi.response.ExchangeRateResponse;
import edu.itba.class1.exchange.client.currencyapi.response.HistoricalExchangeRateResponse;
import edu.itba.class1.exchange.client.error.AuthenticationFailedException;
import edu.itba.class1.exchange.client.error.CurrencyProviderException;
import edu.itba.class1.exchange.client.error.CurrencyProviderRateLimitException;
import edu.itba.class1.exchange.client.error.CurrencyProviderResourceNotFoundException;
import edu.itba.class1.exchange.client.error.InvalidProviderRequestException;
import edu.itba.class1.exchange.client.error.InvalidProviderResponseException;
import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.http.HttpStatus;
import edu.itba.class1.exchange.http.ResponseStatusChecker;
import edu.itba.class1.exchange.parser.JsonParser;
import edu.itba.class1.exchange.parser.JsonParseException;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.Map;

@RequiredArgsConstructor
public class FreeCurrencyApiClient implements CurrencyApiClient {
    private final HttpClient httpClient;
    private final JsonParser jsonParser;
    private final ResponseStatusChecker responseStatusChecker;
    private static final String BASE_URL = "https://api.freecurrencyapi.com/v1";
    private static final String API_KEY = "fca_live_lOuYu1BdnuDDXjOBIHYivtJ2qEKZPgjpc0GtN7hV";
    private static final Map<String, String> API_HEADERS = Map.of("accept", "application/json", "apikey", API_KEY);

    public FreeCurrencyApiClient(HttpClient httpClient, JsonParser jsonParser) {
        this(httpClient, jsonParser, defaultStatusChecker());
    }

    public static ResponseStatusChecker defaultStatusChecker() {
        return new ResponseStatusChecker(
                Map.of(
                        HttpStatus.UNAUTHORIZED.code(), AuthenticationFailedException::new,
                        HttpStatus.FORBIDDEN.code(), AuthenticationFailedException::new,
                        HttpStatus.NOT_FOUND.code(), CurrencyProviderResourceNotFoundException::new,
                        HttpStatus.UNPROCESSABLE_CONTENT.code(), InvalidProviderRequestException::new,
                        HttpStatus.TOO_MANY_REQUESTS.code(), CurrencyProviderRateLimitException::new),
                response -> new CurrencyProviderException("Currency provider request failed", response));
    }

    @Override
    public ExchangeRateResponse getMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies) {
        return this.getParsedResponse(URI.create(BASE_URL.concat("/latest")),
                Map.of("base_currency", fromCurrency.getCurrencyCode(), "currencies", joinCodes(toCurrencies)),
                ExchangeRateResponse.class);
    }

    @Override
    public AvailableCurrenciesResponse getAvailableCurrencies() {
        return this.getParsedResponse(URI.create(BASE_URL.concat("/currencies")), Map.of(), AvailableCurrenciesResponse.class);
    }

    @Override
    public HistoricalExchangeRateResponse getHistoricalMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies, LocalDate date) {
        return this.getParsedResponse(URI.create(BASE_URL.concat("/historical")),
                Map.of("base_currency", fromCurrency.getCurrencyCode(), "currencies", joinCodes(toCurrencies), "date", date.toString()),
                HistoricalExchangeRateResponse.class);
    }

    private static String joinCodes(Collection<Currency> currencies) {
        return String.join(",", currencies.stream().map(Currency::getCurrencyCode).toList());
    }

    private <T> T getParsedResponse(URI uri, Map<String, Object> queryParams, Class<T> responseClass) {
        final var response = this.httpClient.get(uri, queryParams, API_HEADERS);
        this.responseStatusChecker.check(response);
        try {
            return this.jsonParser.parse(response.body(), responseClass);
        } catch (JsonParseException exception) {
            throw new InvalidProviderResponseException(response, exception);
        }
    }
}
