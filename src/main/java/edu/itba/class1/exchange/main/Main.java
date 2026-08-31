import edu.itba.class1.exchange.client.currencyapi.CurrencyApiProvider;
import edu.itba.class1.exchange.client.currencyapi.FreeCurrencyApiClient;
import edu.itba.class1.exchange.client.currencyapi.ResponseStatusChecker;
import edu.itba.class1.exchange.client.error.AuthenticationFailedException;
import edu.itba.class1.exchange.client.error.CurrencyProviderException;
import edu.itba.class1.exchange.client.error.CurrencyProviderRateLimitException;
import edu.itba.class1.exchange.client.error.CurrencyProviderResourceNotFoundException;
import edu.itba.class1.exchange.client.error.InvalidProviderRequestException;
import edu.itba.class1.exchange.http.UnirestHttpClient;
import edu.itba.class1.exchange.model.MoneyAmount;
import edu.itba.class1.exchange.parser.GsonJsonParser;
import edu.itba.class1.exchange.service.CurrencyConverter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

void main() {
	final var httpClient = new UnirestHttpClient();
	final var parser = new GsonJsonParser();
	final var responseStatusChecker = new ResponseStatusChecker(
			Map.of(
					401, AuthenticationFailedException::new,
					403, AuthenticationFailedException::new,
					404, CurrencyProviderResourceNotFoundException::new,
					422, InvalidProviderRequestException::new,
					429, CurrencyProviderRateLimitException::new),
			() -> new CurrencyProviderException("Currency provider request failed"));
	final var currencyApiClient = new FreeCurrencyApiClient(httpClient, parser, responseStatusChecker);
	final var provider = new CurrencyApiProvider(currencyApiClient);
	final var converter = new CurrencyConverter(provider);
	final var ars = new MoneyAmount(Currency.getInstance("ARS"), BigDecimal.valueOf(100.0));
}
