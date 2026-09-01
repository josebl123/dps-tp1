package edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response;

import java.util.Collection;
import java.util.Currency;
import java.util.Map;

import lombok.Setter;

@Setter
public class AvailableCurrenciesResponse {
    private Map<String, CurrencyData> data;

	public Collection<Currency> getCurrencies() {
		final var availableCurrencies = this.data.keySet();
        return availableCurrencies.stream().map(Currency::getInstance).toList();
    }

	private record CurrencyData(String code) {}
}
