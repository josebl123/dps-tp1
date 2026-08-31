package edu.itba.class1.exchange.service;

import edu.itba.class1.exchange.model.Conversion;
import edu.itba.class1.exchange.model.MoneyAmount;


import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;

@RequiredArgsConstructor
public class CurrencyConverter {

	private final CurrencyRateProvider currencyRateProvider;

	public Conversion convert(MoneyAmount moneyAmount, Currency to) {
		final var rate = this.currencyRateProvider.getCurrencyRate(moneyAmount.currency(), to);
		return new Conversion(new MoneyAmount(to, moneyAmount.multiply(rate.rate())), rate);
	}
	public Collection<Conversion> convertMultiple(MoneyAmount moneyAmount, Collection<Currency> to) {
		final var rates = this.currencyRateProvider.getMultipleCurrencyRates(moneyAmount.currency(), to);
		return rates.stream()
				.map(rate -> new Conversion(new MoneyAmount(rate.toCurrency(), moneyAmount.multiply(rate.rate())), rate))
				.toList();
	}
	public Conversion historicalConvert(MoneyAmount moneyAmount, Currency to, LocalDate date){
		final var rate = this.currencyRateProvider.getHistoricalCurrencyRate(moneyAmount.currency(), to, date);
		return new Conversion(new MoneyAmount(to, moneyAmount.multiply(rate.rate())), rate);
	}

	public Collection<Conversion> historicalMultipleConvert(MoneyAmount moneyAmount,Collection<Currency> to,LocalDate date){
		final var rates = this.currencyRateProvider.getHistoricalMultipleCurrencyRates(moneyAmount.currency(), to, date);
		return rates.stream()
				.map(rate -> new Conversion(new MoneyAmount(rate.toCurrency(), moneyAmount.multiply(rate.rate())), rate))
				.toList();
	}
}
