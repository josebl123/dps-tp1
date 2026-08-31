package edu.itba.class1.exchange.client.error;

public class CurrencyProviderException extends RuntimeException {
    public CurrencyProviderException(String message) {
        super(message);
    }

    public CurrencyProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
