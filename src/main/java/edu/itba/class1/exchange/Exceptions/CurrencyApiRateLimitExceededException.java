package edu.itba.class1.exchange.Exceptions;

public class CurrencyApiRateLimitExceededException extends CurrencyRateProviderException {
    public CurrencyApiRateLimitExceededException() {
        super("Se superó el límite de requests o la cuota mensual");
    }
}