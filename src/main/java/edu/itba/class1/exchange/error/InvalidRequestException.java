package edu.itba.class1.exchange.error;

public class InvalidRequestException extends CurrencyApiException {
    public InvalidRequestException(int statusCode, String responseBody) {
        super(statusCode, responseBody);
    }
}
