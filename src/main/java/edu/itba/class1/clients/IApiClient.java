package edu.itba.class1.clients;

import java.math.BigDecimal;
import java.util.Currency;

import edu.itba.class1.models.Money;

public interface IApiClient {
    BigDecimal getExchangeRate(Money money, Currency toCurrency);
}
