package edu.itba.class1.exchange;



import edu.itba.class1.exchange.http.HttpResponse;
import edu.itba.class1.exchange.parser.JsonParser;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.List;


@AllArgsConstructor
public class CurrencyApiProvider implements CurrencyRateProvider, CurrencyCatalog {
	private final CurrencyApiClient currencyApiClient;
	private final JsonParser jsonParser;

	@Override
	public CurrencyRate getCurrencyRate(Currency from, Currency to) {
		return this.getMultipleCurrencyRates(from, List.of(to)).stream().findFirst().orElseThrow();
	}

	@Override
	public Collection<CurrencyRate> getMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies) {
		final var response = this.currencyApiClient.getMultipleCurrencyRates(fromCurrency, toCurrencies);
		validateResponse(response);
		final var exchangeRateResponse = this.jsonParser.parse(response.body(), ExchangeRateResponse.class);
		return toCurrencies.stream().map(toCurrency -> new CurrencyRate(fromCurrency, toCurrency, exchangeRateResponse.getExchange(toCurrency.getCurrencyCode())))
				.toList();
	}

	@Override
	public Collection<CurrencyRate> getHistoricalMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies, LocalDate date) {
		final var response = this.currencyApiClient.getHistoricalMultipleCurrencyRates(fromCurrency, toCurrencies, date);
		validateResponse(response);
		final var exchangeRateResponse = this.jsonParser.parse(response.body(), ExchangeRateResponse.class);
		return toCurrencies.stream().map(toCurrency -> new CurrencyRate(fromCurrency, toCurrency,  exchangeRateResponse.getExchange(toCurrency.getCurrencyCode())))
				.toList();

	}

	@Override
	public Collection<Currency> getAvailableCurrencies() {
		final var response = this.getAllCurrencies();
		validateResponse(response);
		return this.jsonParser.parse(response.body(), AvailableCurrenciesResponse.class).getCurrencies();
	}

	private void validateResponse(HttpResponse response) {
		// TODO: implementar el manejo de errores de la API.
	}

	private HttpResponse getAllCurrencies() {
		return this.currencyApiClient.getAvailableCurrencies();
	}

}
