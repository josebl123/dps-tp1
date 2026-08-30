import edu.itba.class1.exchange.*;
import edu.itba.class1.exchange.parser.GsonJsonParser;

import com.google.gson.Gson;

void main() {
	final var httpClient = new UnirestHttpClient();
	final var parser = new GsonJsonParser();
	final var currencyApiClient = new FreeCurrencyApiClient(httpClient, parser);
	final var provider = new CurrencyApiProvider(currencyApiClient);
	final var converter = new CurrencyConverter(provider);
	final var ars = new MoneyAmount(Currency.getInstance("ARS"), BigDecimal.valueOf(100.0));
}
