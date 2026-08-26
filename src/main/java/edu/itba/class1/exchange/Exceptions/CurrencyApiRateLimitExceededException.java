package edu.itba.class1.exchange.Exceptions;

public class CurrencyApiRateLimitExceededException extends RuntimeException {
    public CurrencyApiRateLimitExceededException(String message) {
        super(message);
    }
}
