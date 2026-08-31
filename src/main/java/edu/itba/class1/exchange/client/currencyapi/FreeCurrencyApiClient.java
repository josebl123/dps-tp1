package edu.itba.class1.exchange.client.currencyapi;

import edu.itba.class1.exchange.client.currencyapi.response.AvailableCurrenciesResponse;
import edu.itba.class1.exchange.client.currencyapi.response.ExchangeRateResponse;
import edu.itba.class1.exchange.client.error.AuthenticationFailedException;
import edu.itba.class1.exchange.client.error.CurrencyProviderException;
import edu.itba.class1.exchange.client.error.CurrencyProviderRateLimitException;
import edu.itba.class1.exchange.client.error.CurrencyProviderResourceNotFoundException;
import edu.itba.class1.exchange.client.error.InvalidProviderRequestException;
import edu.itba.class1.exchange.client.error.InvalidProviderResponseException;
import edu.itba.class1.exchange.http.HttpClient;
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
    private static final String BASE_URL = "https://api.currencyapi.com/v3";
    private static final String API_KEY = "cur_live_33r7xmpvu4kiAmeK4XZrotAn84qT4SMnPlP9Use1";
    private static final Map<String, String> API_HEADERS = Map.of("accept", "application/json", "apikey", API_KEY);

    public FreeCurrencyApiClient(HttpClient httpClient, JsonParser jsonParser) {
        this(httpClient, jsonParser, defaultStatusChecker());
    }

    public static ResponseStatusChecker defaultStatusChecker() {
        return new ResponseStatusChecker(
                Map.of(
                        401, AuthenticationFailedException::new,
                        403, AuthenticationFailedException::new,
                        404, CurrencyProviderResourceNotFoundException::new,
                        422, InvalidProviderRequestException::new,
                        429, CurrencyProviderRateLimitException::new),
                response -> new CurrencyProviderException("Currency provider request failed", response));
    }

    @Override
    public ExchangeRateResponse getMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies) {
        return this.getParsedResponse(URI.create(BASE_URL + "/latest"),
                Map.of("base_currency", fromCurrency.getCurrencyCode(), "currencies", joinCodes(toCurrencies)),
                ExchangeRateResponse.class);
    }

    @Override
    public AvailableCurrenciesResponse getAvailableCurrencies() {
        return this.getParsedResponse(URI.create(BASE_URL + "/currencies"), Map.of(), AvailableCurrenciesResponse.class);
    }

    @Override
    public ExchangeRateResponse getHistoricalMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies, LocalDate date) {
        return this.getParsedResponse(URI.create(BASE_URL + "/historical"),
                Map.of("base_currency", fromCurrency.getCurrencyCode(), "currencies", joinCodes(toCurrencies), "date", date.toString()),
                ExchangeRateResponse.class);
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
