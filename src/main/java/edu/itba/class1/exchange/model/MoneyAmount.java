package edu.itba.class1.exchange.model;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record MoneyAmount(Currency currency, BigDecimal amount) {

	public MoneyAmount {
		if (currency == null) {
			throw new IllegalArgumentException("Currency cannot be null");
		}
		if (amount == null) {
			throw new IllegalArgumentException("Amount cannot be null");
		}
		amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
	}

	public MoneyAmount convertWithRate(CurrencyRate rate) {
		return new MoneyAmount(rate.toCurrency(), this.amount.multiply(rate.rate()));
	}

	public MoneyAmount add(MoneyAmount other) {
		if (!this.currency.equals(other.currency)) {
			throw new IllegalArgumentException("Cannot add amounts with different currencies");
		}
		return new MoneyAmount(this.currency, this.amount.add(other.amount));
	}


	public BigDecimal multiply(BigDecimal rate) {
		return this.amount.multiply(rate);
	}
}
