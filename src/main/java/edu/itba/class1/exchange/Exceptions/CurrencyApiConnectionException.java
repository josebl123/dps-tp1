package edu.itba.class1.exchange.Exceptions;

public class CurrencyApiConnectionException extends RuntimeException {
    public CurrencyApiConnectionException(String message) {
        super(message);
    }
}
