package edu.itba.class1.exchange.service;

import edu.itba.class1.exchange.client.CurrencyApiClient;
import edu.itba.class1.exchange.model.CurrencyRate;
import edu.itba.class1.exchange.model.ExchangeRateResponse;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.List;


@AllArgsConstructor
public class CurrencyApiProvider implements CurrencyRateProvider, CurrencyCatalog {
	private final CurrencyApiClient currencyApiClient;

	@Override
	public CurrencyRate getCurrencyRate(Currency from, Currency to) {
		return this.getMultipleCurrencyRates(from, List.of(to)).stream().findFirst().orElseThrow();
	}

	@Override
	public Collection<CurrencyRate> getMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies) {
		final var exchangeRateResponse = this.currencyApiClient.getMultipleCurrencyRates(fromCurrency, toCurrencies);
		return toCurrencies.stream().map(toCurrency -> new CurrencyRate(fromCurrency, toCurrency, exchangeRateResponse.getExchange(toCurrency.getCurrencyCode())))
				.toList();
	}

	@Override
	public CurrencyRate getHistoricalCurrencyRate(Currency from, Currency to, LocalDate date) {
		return this.getHistoricalMultipleCurrencyRates(from, List.of(to), date).stream().findFirst().orElseThrow();
	}

	@Override
	public Collection<CurrencyRate> getHistoricalMultipleCurrencyRates(Currency fromCurrency, Collection<Currency> toCurrencies, LocalDate date) {
		final var exchangeRateResponse = this.currencyApiClient.getHistoricalMultipleCurrencyRates(fromCurrency, toCurrencies, date);
		return toCurrencies.stream().map(toCurrency -> new CurrencyRate(fromCurrency, toCurrency, exchangeRateResponse.getExchange(toCurrency.getCurrencyCode()), date))
				.toList();

	}

	@Override
	public Collection<Currency> getAvailableCurrencies() {
		final var availableCurrenciesResponse = this.currencyApiClient.getAvailableCurrencies();
		return availableCurrenciesResponse.getCurrencies();
	}

}
