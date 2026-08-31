package edu.itba.class1.exchange.client.currencyapi.response;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import lombok.Setter;

@Setter
public class ExchangeRateResponse {
    private Map<String, CurrencyData> data;

	public Optional<BigDecimal> findExchange(final String toCurrency) {
		return Optional.ofNullable(data)
				.map(rates -> rates.get(toCurrency))
				.map(CurrencyData::value);
	}

	private record CurrencyData(String code, BigDecimal value) {}
}
