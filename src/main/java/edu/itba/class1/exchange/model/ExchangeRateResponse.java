package edu.itba.class1.exchange.model;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Setter;

@Setter
public class ExchangeRateResponse {
    private Map<String, CurrencyData> data;

	public BigDecimal getExchange(final String toCurrency) {
		final var currencyData = this.data.get(toCurrency);
		if (currencyData == null) {
			throw new IllegalStateException("Missing exchange rate for currency: " + toCurrency);
		}
		return currencyData.value;
	}

	private record CurrencyData(String code, BigDecimal value) {}
}
