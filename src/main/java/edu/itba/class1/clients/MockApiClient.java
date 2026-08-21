package edu.itba.class1.clients;

import java.math.BigDecimal;
import java.util.Currency;

import edu.itba.class1.models.Money;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MockApiClient implements IApiClient{

    private final BigDecimal exchangeRate = new BigDecimal(2);

    @Override
    public BigDecimal getExchangeRate(Money money, Currency toCurrency) {
        return exchangeRate;
    }
    
}
