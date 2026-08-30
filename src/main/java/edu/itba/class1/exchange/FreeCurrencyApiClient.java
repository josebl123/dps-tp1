package edu.itba.class1.exchange;

import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.parser.JsonParser;
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
    private static final String API_KEY = "cur_live_33r7xmpvu4kiAmeK4XZrotAn84qT4SMnPlP9Use1";

    @Override
    public ExchangeRateResponse getCurrencyRate(Currency fromCurrency, Currency toCurrency) {
        final var response = this.httpClient.get(URI.create("https://api.currencyapi.com/v3/latest"),
                Map.of("base_currency", fromCurrency, "currencies", toCurrency),
                Map.of("accept", "application/json", "apikey", API_KEY));
        return this.jsonParser.parse(response.body(), ExchangeRateResponse.class);
    }

    @Override
    public ExchangeRateResponse getMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies) {
        final var response = this.httpClient.get(URI.create("https://api.currencyapi.com/v3/latest"),
                Map.of("base_currency", fromCurrency, "currencies", String.join(",", toCurrencies.stream().map(Currency::getCurrencyCode).toList())),
                Map.of("accept", "application/json", "apikey", API_KEY));
        return this.jsonParser.parse(response.body(), ExchangeRateResponse.class);
    }

    @Override
    public AvailableCurrenciesResponse getAvailableCurrencies() {
        final var response = this.httpClient.get(URI.create("https://api.currencyapi.com/v3/currencies"),
                Map.of(),
                Map.of("accept", "application/json", "apikey", API_KEY));
        return this.jsonParser.parse(response.body(), AvailableCurrenciesResponse.class);
    }

    @Override
    public ExchangeRateResponse getHistoricalMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies, LocalDate date) {
        final var response = this.httpClient.get(URI.create("https://api.currencyapi.com/v3/historical"),
                Map.of("base_currency", fromCurrency, "currencies", String.join(",", toCurrencies.stream().map(Currency::getCurrencyCode).toList()), "date", date.toString()),
                Map.of("accept", "application/json", "apikey", API_KEY));
        return this.jsonParser.parse(response.body(), ExchangeRateResponse.class);
    }
}
