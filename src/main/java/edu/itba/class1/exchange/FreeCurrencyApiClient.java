package edu.itba.class1.exchange;

import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.parser.JsonParser;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.Map;

import org.apache.http.HttpStatus;

@RequiredArgsConstructor
public class FreeCurrencyApiClient implements CurrencyApiClient {

    private final HttpClient httpClient;
    private final JsonParser jsonParser;
    private static final String API_KEY = "cur_live_33r7xmpvu4kiAmeK4XZrotAn84qT4SMnPlP9Use1";
    private static final Map<String, String> API_HEADERS = Map.of("accept", "application/json", "apikey", API_KEY);

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
        if (response.statusCode() != HttpStatus.SC_OK) {
            throw new CurrencyApiException(response.statusCode(), response.body());
        }
        return this.jsonParser.parse(response.body(), responseClass);
    }
}
