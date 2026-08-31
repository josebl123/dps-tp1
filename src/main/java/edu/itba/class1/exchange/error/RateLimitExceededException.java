package edu.itba.class1.exchange.error;

public class RateLimitExceededException extends CurrencyApiException {
    public RateLimitExceededException(int statusCode, String responseBody) {
        super(statusCode, responseBody);
    }
}
