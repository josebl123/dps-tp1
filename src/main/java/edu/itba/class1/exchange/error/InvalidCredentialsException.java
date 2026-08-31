package edu.itba.class1.exchange.error;

public class InvalidCredentialsException extends CurrencyApiException {
    public InvalidCredentialsException(int statusCode, String responseBody) {
        super(statusCode, responseBody);
    }
}
