package edu.itba.class1.exchange.client.currencyapi.freecurrencyapi;

import edu.itba.class1.exchange.client.currencyapi.CurrencyApiClient;
import edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response.AvailableCurrenciesResponse;
import edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response.ExchangeRateResponse;
import edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response.HistoricalExchangeRateResponse;
import edu.itba.class1.exchange.client.error.AuthenticationFailedException;
import edu.itba.class1.exchange.client.error.CurrencyProviderException;
import edu.itba.class1.exchange.client.error.CurrencyProviderRateLimitException;
import edu.itba.class1.exchange.client.error.CurrencyProviderResourceNotFoundException;
import edu.itba.class1.exchange.client.error.InvalidProviderRequestException;
import edu.itba.class1.exchange.client.error.InvalidProviderResponseException;
import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.http.HttpStatus;
import edu.itba.class1.exchange.http.ResponseStatusChecker;
import edu.itba.class1.exchange.model.CurrencyRate;
import edu.itba.class1.exchange.parser.JsonParser;
import edu.itba.class1.exchange.parser.JsonParseException;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class FreeCurrencyApiClient implements CurrencyApiClient {
    private final HttpClient httpClient;
    private final JsonParser jsonParser;
    private final ResponseStatusChecker responseStatusChecker;
    private final FreeCurrencyResponseMapper responseMapper;
    private final String apiKey;
    private static final String BASE_URL = "https://api.freecurrencyapi.com/v1";

    public FreeCurrencyApiClient(HttpClient httpClient, JsonParser jsonParser, String apiKey) {
        this(httpClient, jsonParser, defaultStatusChecker(), new FreeCurrencyResponseMapper(), apiKey);
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
    public Collection<CurrencyRate> getRates(Currency fromCurrency, Collection<Currency> toCurrencies) {
        final var response = this.getParsedResponse(URI.create(BASE_URL.concat("/latest")),
                Map.of("base_currency", fromCurrency.getCurrencyCode(), "currencies", joinCodes(toCurrencies)),
                ExchangeRateResponse.class);
        return this.responseMapper.toRates(fromCurrency, toCurrencies, response);
    }

    @Override
    public Collection<Currency> getAvailableCurrencies() {
        return this.responseMapper.toCurrencies(
                this.getParsedResponse(URI.create(BASE_URL.concat("/currencies")), Map.of(), AvailableCurrenciesResponse.class));
    }

    @Override
    public Collection<CurrencyRate> getHistoricalRates(Currency fromCurrency, Collection<Currency> toCurrencies, LocalDate date) {
        final var response = this.getParsedResponse(URI.create(BASE_URL.concat("/historical")),
                Map.of("base_currency", fromCurrency.getCurrencyCode(), "currencies", joinCodes(toCurrencies), "date", date.toString()),
                HistoricalExchangeRateResponse.class);
        return this.responseMapper.toHistoricalRates(fromCurrency, toCurrencies, date, response);
    }

    private static String joinCodes(Collection<Currency> currencies) {
        return String.join(",", currencies.stream().map(Currency::getCurrencyCode).toList());
    }

    private Map<String, String> apiHeaders() {
        return Map.of("accept", "application/json", "apikey", Objects.requireNonNull(this.apiKey, "apiKey"));
    }

    private <T> T getParsedResponse(URI uri, Map<String, Object> queryParams, Class<T> responseClass) {
        final var response = this.httpClient.get(uri, queryParams, this.apiHeaders());
        this.responseStatusChecker.check(response);
        try {
            return this.jsonParser.parse(response.body(), responseClass);
        } catch (JsonParseException exception) {
            throw new InvalidProviderResponseException(response, exception);
        }
    }
}
