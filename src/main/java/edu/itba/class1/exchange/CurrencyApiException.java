package edu.itba.class1.exchange;

public class CurrencyApiException extends RuntimeException {

    private final int statusCode;

    public CurrencyApiException(int statusCode, String responseBody) {
        super("Currency API request failed with status %d: %s".formatted(statusCode, responseBody));
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return this.statusCode;
    }
}
