package edu.itba.class1.exchange.error;

public class ResourceNotFoundException extends CurrencyApiException {
    public ResourceNotFoundException(int statusCode, String responseBody) {
        super(statusCode, responseBody);
    }
}
