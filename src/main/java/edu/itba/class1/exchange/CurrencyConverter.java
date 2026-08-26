package edu.itba.class1.exchange;



import edu.itba.class1.exchange.Providers.CurrencyRateProvider;
import lombok.RequiredArgsConstructor;

import java.util.Currency;

@RequiredArgsConstructor
public class CurrencyConverter {

	private final CurrencyRateProvider currencyRateProvider;

	public MoneyAmount convert(MoneyAmount moneyAmount, Currency to) {
		final var rate = this.currencyRateProvider.getCurrencyRate(moneyAmount.currency(), to).rate();
		return new MoneyAmount(to, moneyAmount.multiply(rate));
	}

}
