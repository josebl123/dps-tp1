import edu.itba.class1.exchange.client.currencyapi.CurrencyApiProvider;
import edu.itba.class1.exchange.client.currencyapi.FreeCurrencyApiClient;
import edu.itba.class1.exchange.http.UnirestHttpClient;
import edu.itba.class1.exchange.model.MoneyAmount;
import edu.itba.class1.exchange.parser.GsonJsonParser;
import edu.itba.class1.exchange.service.CurrencyConverter;

import java.math.BigDecimal;
import java.util.Currency;

void main() {
	final var httpClient = new UnirestHttpClient();
	final var parser = new GsonJsonParser();
	final var currencyApiClient = new FreeCurrencyApiClient(httpClient, parser);
	final var provider = new CurrencyApiProvider(currencyApiClient);
	final var converter = new CurrencyConverter(provider);
	final var ars = new MoneyAmount(Currency.getInstance("ARS"), BigDecimal.valueOf(100.0));
}
