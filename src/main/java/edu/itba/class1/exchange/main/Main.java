import edu.itba.class1.exchange.client.currencyapi.CurrencyApiProvider;
import edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.FreeCurrencyApiClient;
import edu.itba.class1.exchange.client.error.CurrencyProviderException;
import edu.itba.class1.exchange.http.HttpTransportException;
import edu.itba.class1.exchange.http.UnirestHttpClient;
import edu.itba.class1.exchange.model.Conversion;
import edu.itba.class1.exchange.model.MoneyAmount;
import edu.itba.class1.exchange.parser.GsonJsonParser;
import edu.itba.class1.exchange.service.CurrencyConverter;
import edu.itba.class1.exchange.service.HistoricalCurrencyConverter;
import edu.itba.class1.exchange.service.error.RateNotAvailableException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

static final String API_KEY_VARIABLE = "FREECURRENCYAPI_KEY";
static final Path ENV_FILE = Path.of(".env");

void main() {
	final var apiKey = readApiKey();
	if (apiKey == null || apiKey.isBlank()) {
		System.err.print("Falta  key de freecurrencyapi.com.");
		return;
	}

	final var provider = new CurrencyApiProvider(
			new FreeCurrencyApiClient(new UnirestHttpClient(), new GsonJsonParser(), apiKey));
	final var converter = new CurrencyConverter(provider);
	final var historicalConverter = new HistoricalCurrencyConverter(provider);

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
		historicalConverter.convertMultiple(amount, List.of(eur, jpy), date)
				.forEach(this::print);

	} catch (final CurrencyProviderException | HttpTransportException | RateNotAvailableException e) {
		System.err.println("No se pudo completar la operacion: " + e.getMessage());
	}
}

String readApiKey() {
	final var fromFile = readEnvFile().get(API_KEY_VARIABLE);
	return fromFile != null ? fromFile : System.getenv(API_KEY_VARIABLE);
}

Map<String, String> readEnvFile() {
	if (!Files.exists(ENV_FILE)) {
		return Map.of();
	}
	try {
		return Files.readAllLines(ENV_FILE).stream()
				.map(String::trim)
				.filter(line -> !line.isEmpty() && !line.startsWith("#") && line.contains("="))
				.collect(Collectors.toMap(
						line -> line.substring(0, line.indexOf('=')).trim(),
						line -> unquote(line.substring(line.indexOf('=') + 1).trim()),
						(first, second) -> second));
	} catch (final IOException e) {
		System.err.printf("No se pudo leer %s: %s%n", ENV_FILE, e.getMessage());
		return Map.of();
	}
}

String unquote(final String value) {
	final var quoted = value.length() >= 2
			&& (value.startsWith("\"") && value.endsWith("\"") || value.startsWith("'") && value.endsWith("'"));
	return quoted ? value.substring(1, value.length() - 1) : value;
}

void print(final Conversion conversion) {
	final var rate = conversion.rateUsed();
	System.out.printf("  %s  (cotizacion %s -> %s: %s, del %s)%n",
			conversion.converted(), rate.fromCurrency(), rate.toCurrency(), rate.rate(), rate.timestamp());
}
