package edu.itba.class1.exchange.client;

import edu.itba.class1.exchange.error.resolver.GenericResponseErrorResolver;
import edu.itba.class1.exchange.error.resolver.InvalidCredentialsResponseErrorResolver;
import edu.itba.class1.exchange.error.resolver.InvalidRequestResponseErrorResolver;
import edu.itba.class1.exchange.error.resolver.RateLimitResponseErrorResolver;
import edu.itba.class1.exchange.error.resolver.ResourceNotFoundResponseErrorResolver;
import edu.itba.class1.exchange.error.resolver.ResponseErrorResolverChain;
import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.parser.JsonParser;
import edu.itba.class1.exchange.model.AvailableCurrenciesResponse;
import edu.itba.class1.exchange.model.ExchangeRateResponse;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.Map;

public class FreeCurrencyApiClient implements CurrencyApiClient {

    private final HttpClient httpClient;
    private final JsonParser jsonParser;
    private final ResponseErrorResolverChain responseErrorResolverChain;
    private static final String API_KEY = "cur_live_33r7xmpvu4kiAmeK4XZrotAn84qT4SMnPlP9Use1";
    private static final Map<String, String> API_HEADERS = Map.of("accept", "application/json", "apikey", API_KEY);

    public FreeCurrencyApiClient(HttpClient httpClient, JsonParser jsonParser) {
        this(httpClient, jsonParser, defaultResponseErrorResolverChain());
    }

    public FreeCurrencyApiClient(HttpClient httpClient, JsonParser jsonParser,
                                 ResponseErrorResolverChain responseErrorResolverChain) {
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.responseErrorResolverChain = responseErrorResolverChain;
    }

    private static ResponseErrorResolverChain defaultResponseErrorResolverChain() {
        return new ResponseErrorResolverChain(java.util.List.of(
                new InvalidCredentialsResponseErrorResolver(),
                new ResourceNotFoundResponseErrorResolver(),
                new InvalidRequestResponseErrorResolver(),
                new RateLimitResponseErrorResolver()));
    }

    @Override
    public ExchangeRateResponse getCurrencyRate(Currency fromCurrency, Currency toCurrency) {
        return this.getParsedResponse(
            URI.create("https://api.currencyapi.com/v3/latest"), 
            Map.of("base_currency", fromCurrency, "currencies", toCurrency), 
            ExchangeRateResponse.class
        );
    }

    @Override
    public ExchangeRateResponse getMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies) {
        return this.getParsedResponse(
            URI.create("https://api.currencyapi.com/v3/latest"),
            Map.of("base_currency", fromCurrency, "currencies", String.join(",", toCurrencies.stream().map(Currency::getCurrencyCode).toList())),
            ExchangeRateResponse.class
        );
    }

    @Override
    public AvailableCurrenciesResponse getAvailableCurrencies() {
        return this.getParsedResponse(
            URI.create("https://api.currencyapi.com/v3/currencies"),
             Map.of(),
            AvailableCurrenciesResponse.class
        );
    }

    @Override
    public ExchangeRateResponse getHistoricalMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies, LocalDate date) {
        return this.getParsedResponse(
            URI.create("https://api.currencyapi.com/v3/historical"),
            Map.of("base_currency", fromCurrency, "currencies", String.join(",", toCurrencies.stream().map(Currency::getCurrencyCode).toList()), "date", date.toString()),
            ExchangeRateResponse.class
        );
    }

    private <T> T getParsedResponse(URI uri, Map<String, Object> queryParams, Class<T> responseClass) {
        final var response = this.httpClient.get(uri, queryParams, API_HEADERS);
        this.responseErrorResolverChain.resolve(response);
        return this.jsonParser.parse(response.body(), responseClass);
    }
}
