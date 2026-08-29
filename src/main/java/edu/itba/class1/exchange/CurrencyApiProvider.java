package edu.itba.class1.exchange;



import com.google.gson.Gson;

import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.http.HttpResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
public class CurrencyApiProvider implements CurrencyRateProvider, CurrencyCatalog {
	private final CurrencyApiClient currencyApiClient;

	@Override
	public CurrencyRate getCurrencyRate(Currency from, Currency to) {
		return this.getMultipleCurrencyRates(from, List.of(to)).stream().findFirst().orElseThrow();
	}

	@Override
	public Collection<CurrencyRate> getMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies) {
		final var response = this.currencyApiClient.getMultipleCurrencyRates(fromCurrency, toCurrencies);
		validateResponse(response);
		final var exchangeRateResponse = this.getResponse(response, ExchangeRateResponse.class);
		return toCurrencies.stream().map(toCurrency -> new CurrencyRate(fromCurrency, toCurrency, exchangeRateResponse.getExchange(toCurrency.getCurrencyCode())))
				.toList();
	}

	@Override
	public Collection<CurrencyRate> getHistoricalMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies, LocalDate date) {
		final var response = this.currencyApiClient.getHistoricalMultipleCurrencyRates(fromCurrency, toCurrencies, date);
		validateResponse(response);
		final var exchangeRateResponse = this.getResponse(response, ExchangeRateResponse.class);
		return toCurrencies.stream().map(toCurrency -> new CurrencyRate(fromCurrency, toCurrency,  exchangeRateResponse.getExchange(toCurrency.getCurrencyCode())))
				.toList();

	}

	@Override
	public Collection<Currency> getAvailableCurrencies() {
		final var response = this.getAllCurrencies();
		validateResponse(response);
		return this.getResponse(response, AvailableCurrenciesResponse.class).getCurrencies();
	}

	private void validateResponse(HttpResponse response) {
		//throw if not 200 and proper notfound or forbidden or whatever exception
		//possibly no ifs and use a similar concept to a factory for the different
		// status codes (though not quite the same as a factory)

		//this will resolve a future requirement as well (point 4)
	}

	private <T> T getResponse(HttpResponse response, Class<T> responseType) {
    	return new Gson().fromJson(response.body(), responseType);
	}

	private HttpResponse getConversionRate(Currency fromCurrency, Currency toCurrency) {
		return this.currencyApiClient.getCurrencyRate(fromCurrency, toCurrency);
	}

	private HttpResponse getAllCurrencies() {
		return this.currencyApiClient.getAvailableCurrencies();
	}

}
