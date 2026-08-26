package edu.itba.class1.exchange.Providers;

import edu.itba.class1.exchange.CurrencyRate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public interface HistoricalExchangeRateProvider {
    Map<Currency, CurrencyRate> getHistoricalRates(Currency base, Set<Currency> targets, LocalDate date);
}

