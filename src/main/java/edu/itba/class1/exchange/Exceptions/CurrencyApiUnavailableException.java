package edu.itba.class1.exchange.Exceptions;

public class CurrencyApiUnavailableException extends RuntimeException {
    public CurrencyApiUnavailableException(String message) {
        super(message);
    }

    public static class CurrencyRateNotAvailable extends RuntimeException {
    }
}
