package edu.itba.class1.exchange;

import java.util.Collection;
import java.util.Currency;
import java.util.Map;

import lombok.Setter;

@Setter
public class AvailableCurrenciesResponse {
    
    private Map<String, CurrencyData> data;

	public Collection<Currency> getCurrencies() {
		final var availableCurrencies = this.data.keySet();
		if (availableCurrencies == null) {
			throw new IllegalStateException("No available currencies");
		}
		return availableCurrencies.stream().map(Currency::getInstance).toList(); //should check for error? (Currency::getInstance)
	}

	private record CurrencyData(String code, Object currencyInfo) {}
}
