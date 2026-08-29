import edu.itba.class1.exchange.*;

void main() {
	final var httpClient = new UnirestHttpClient();
	final var currencyApiClient = new FreeCurrencyApiClient(httpClient);
	final var provider = new CurrencyApiProvider(currencyApiClient);
	final var converter = new CurrencyConverter(provider);
	final var ars = new MoneyAmount(Currency.getInstance("ARS"), BigDecimal.valueOf(100.0));
}
