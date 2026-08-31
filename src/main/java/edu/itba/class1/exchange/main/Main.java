import edu.itba.class1.exchange.client.currencyapi.CurrencyApiProvider;
import edu.itba.class1.exchange.client.currencyapi.FreeCurrencyApiClient;
import edu.itba.class1.exchange.http.UnirestHttpClient;
import edu.itba.class1.exchange.parser.GsonJsonParser;

import java.util.Currency;
import java.util.List;
import java.time.LocalDate;

void main() {
	var httpClient = new UnirestHttpClient();
	var parser = new GsonJsonParser();
	var client = new FreeCurrencyApiClient(httpClient, parser);
	var provider = new CurrencyApiProvider(client);

	System.out.println(provider.getAvailableCurrencies());
	System.out.println(provider.getMultipleCurrencyRates(
			Currency.getInstance("USD"),
			List.of(Currency.getInstance("EUR"), Currency.getInstance("JPY"))));

	System.out.println(provider.getHistoricalMultipleCurrencyRates(
			Currency.getInstance("USD"),
			List.of(Currency.getInstance("EUR"), Currency.getInstance("JPY")),
			LocalDate.of(2024, 11, 20)));
}
