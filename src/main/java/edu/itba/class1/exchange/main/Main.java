import edu.itba.class1.exchange.client.currencyapi.CurrencyApiProvider;
import edu.itba.class1.exchange.client.currencyapi.FreeCurrencyApiClient;
import edu.itba.class1.exchange.client.error.CurrencyProviderException;
import edu.itba.class1.exchange.http.HttpTransportException;
import edu.itba.class1.exchange.http.UnirestHttpClient;
import edu.itba.class1.exchange.model.Conversion;
import edu.itba.class1.exchange.model.MoneyAmount;
import edu.itba.class1.exchange.parser.GsonJsonParser;
import edu.itba.class1.exchange.service.CurrencyConverter;
import edu.itba.class1.exchange.service.error.RateNotAvailableException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

void main() {
	final var provider = new CurrencyApiProvider(
			new FreeCurrencyApiClient(new UnirestHttpClient(), new GsonJsonParser()));
	final var converter = new CurrencyConverter(provider);

	final var usd = Currency.getInstance("USD");
	final var eur = Currency.getInstance("EUR");
	final var jpy = Currency.getInstance("JPY");
	final var amount = new MoneyAmount(usd, new BigDecimal("100"));
	final var date = LocalDate.of(2024, 11, 20);

	try {
		final var catalog = provider.getAvailableCurrencies();
		System.out.println("Monedas disponibles (" + catalog.size() + "): " + catalog);


		final var rate = provider.getCurrencyRate(usd, eur);
		System.out.printf("%nCotizacion %s -> %s: %s (obtenida %s)%n",
				rate.fromCurrency(), rate.toCurrency(), rate.rate(), rate.timestamp());

		System.out.printf("%nConversion de %s:%n", amount);
		converter.convertMultiple(amount, List.of(eur, jpy)).forEach(this::print);

		System.out.printf("%nConversion de %s al %s:%n", amount, date);
		converter.historicalMultipleConvert(amount, List.of(eur, jpy), date)
				.forEach(this::print);

	} catch (final CurrencyProviderException | HttpTransportException | RateNotAvailableException e) {
		System.err.println("No se pudo completar la operacion: " + e.getMessage());
	}
}

void print(final Conversion conversion) {
	final var rate = conversion.rateUsed();
	System.out.printf("  %s  (cotizacion %s -> %s: %s, del %s)%n",
			conversion.converted(), rate.fromCurrency(), rate.toCurrency(), rate.rate(), rate.timestamp());
}
