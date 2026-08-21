package edu.itba.class1.exchange;

import edu.itba.class1.clients.IApiClient;
import edu.itba.class1.models.Money;
import lombok.AllArgsConstructor;

import java.util.Currency;

@AllArgsConstructor
public class CurrencyConverter {

	private final IApiClient client;

	public Money convert(Money from, Currency toCurrency) {
		return from.multiply(client.getExchangeRate(from, toCurrency)).changeCurrency(toCurrency);
	}

}
