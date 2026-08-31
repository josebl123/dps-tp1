package edu.itba.class1.exchange.service;

import edu.itba.class1.exchange.model.Conversion;
import edu.itba.class1.exchange.model.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;

@RequiredArgsConstructor
public class HistoricalCurrencyConverter {
	private final HistoricalCurrencyRateProvider historicalCurrencyRateProvider;

	public Conversion convert(MoneyAmount moneyAmount, Currency to, LocalDate date) {
		final var rate = this.historicalCurrencyRateProvider.getHistoricalCurrencyRate(moneyAmount.currency(), to, date);
		return new Conversion(moneyAmount.convertWithRate(rate), rate);
	}

	public Collection<Conversion> convertMultiple(MoneyAmount moneyAmount, Collection<Currency> to, LocalDate date) {
		final var rates = this.historicalCurrencyRateProvider.getHistoricalMultipleCurrencyRates(
				moneyAmount.currency(), to, date);
		return rates.stream()
				.map(rate -> new Conversion(moneyAmount.convertWithRate(rate), rate))
				.toList();
	}
}
