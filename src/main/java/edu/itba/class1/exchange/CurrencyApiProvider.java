package edu.itba.class1.exchange;



import com.google.gson.Gson;

import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.http.HttpResponse;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Collection;
import java.util.Currency;
import java.util.Map;


@RequiredArgsConstructor
public class CurrencyApiProvider implements CurrencyRateProvider, CurrencyCatalog {

	private final HttpClient httpClient;
	private static final String API_KEY = "cur_live_33r7xmpvu4kiAmeK4XZrotAn84qT4SMnPlP9Use1";

	@Override
	public CurrencyRate getCurrencyRate(Currency from, Currency to) {
		final var response = this.getConversionRate(from.getCurrencyCode(), to.getCurrencyCode());
		validateResponse(response);
		final var rate = this.getResponse(response, ExchangeRateResponse.class);
		return new CurrencyRate(rate.getExchange(to.getCurrencyCode()));
	}

	private void validateResponse(HttpResponse response) {
		//throw if not 200 and proper notfound or forbidden or whatever exception
		//possibly no ifs and use a similar concept to a factory for the different
		// status codes (though not quite the same as a factory)

		//this will resolve a future requirement as well (point 4)
	}

	@Override
	public Collection<Currency> getAvailableCurrencies() {
		final var response = this.getAllCurrencies();
		validateResponse(response);
		return this.getResponse(response, AvailableCurrenciesResponse.class).getCurrencies();
	}

	private <T> T getResponse(HttpResponse response, Class<T> responseType) {
    	return new Gson().fromJson(response.body(), responseType);
	}

	private HttpResponse getConversionRate(String fromCurrency, String toCurrency) {
		return this.httpClient.get(URI.create("https://api.currencyapi.com/v3/latest"),
				Map.of("base_currency", fromCurrency, "currencies", toCurrency),
				Map.of("accept", "application/json", "apikey", API_KEY));
	}

	private HttpResponse getAllCurrencies() {
		return this.httpClient.get(URI.create("https://api.currencyapi.com/v3/currencies"),
				Map.of(),
				Map.of("accept", "application/json", "apikey", API_KEY));
	}

}
