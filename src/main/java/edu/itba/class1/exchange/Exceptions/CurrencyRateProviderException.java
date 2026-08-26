package edu.itba.class1.exchange.Exceptions;

public abstract class CurrencyRateProviderException extends RuntimeException {
    protected CurrencyRateProviderException(String message) { super(message); }
    protected CurrencyRateProviderException(String message, Throwable cause) { super(message, cause); }
}