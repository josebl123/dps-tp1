package edu.itba.class1.exchange.Exceptions;

public class CurrencyApiAuthenticationException extends RuntimeException {
    public CurrencyApiAuthenticationException(String message) {
        super(message);
    }
}
