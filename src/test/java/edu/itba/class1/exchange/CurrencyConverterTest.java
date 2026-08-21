package edu.itba.class1.exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.itba.class1.clients.IApiClient;
import edu.itba.class1.clients.MockApiClient;
import edu.itba.class1.models.Money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

class CurrencyConverterTest {

	private CurrencyConverter currencyConverter;

	@BeforeEach
	void setup() {
		IApiClient client = new MockApiClient();
		this.currencyConverter = new CurrencyConverter(client);
	}

	@Test
	void testConvert() {
		// Given
		final var money = new Money(Currency.getInstance(Locale.US), new BigDecimal(200));
		final var toCurrency = Currency.getInstance(Locale.CANADA);

		// When
		final var result = currencyConverter.convert(money, toCurrency);

		// Then
		assertThat(result.amount()).isCloseTo(new BigDecimal(400), within(new BigDecimal(0.01)));
	}
}