package edu.itba.class1.exchange.Providers;

import edu.itba.class1.exchange.CurrencyRate;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.List;

public interface HistoricalExchangeRateProvider {
    CurrencyRate getCurrencyRate(Currency from, List<Currency> toCurrencies, BigDecimal amount, Date date);

}

