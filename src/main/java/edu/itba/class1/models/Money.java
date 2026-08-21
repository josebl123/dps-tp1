package edu.itba.class1.models;

import java.math.BigDecimal;
import java.util.Currency;

public record Money(Currency currency, BigDecimal amount) {

    public Money multiply(BigDecimal factor) {
        return new Money(this.currency, this.amount().multiply(factor));
    }

    public Money changeCurrency(Currency toCurrency) {
        return new Money(toCurrency, this.amount);
    }
}
