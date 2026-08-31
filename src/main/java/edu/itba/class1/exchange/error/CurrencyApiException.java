package edu.itba.class1.exchange.error;

public class CurrencyApiException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public CurrencyApiException(int statusCode, String responseBody) {
        super("Currency API request failed with status %d: %s".formatted(statusCode, responseBody));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int statusCode() {
        return this.statusCode;
    }

    public String responseBody() {
        return this.responseBody;
    }

    public String body() {
        return this.responseBody;
    }
}
