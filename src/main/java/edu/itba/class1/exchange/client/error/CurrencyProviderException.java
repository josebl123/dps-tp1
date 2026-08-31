package edu.itba.class1.exchange.client.error;

import edu.itba.class1.exchange.http.HttpResponse;

public class CurrencyProviderException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    public CurrencyProviderException(String message, HttpResponse response) {
        this(message, response, null);
    }

    public CurrencyProviderException(String message, HttpResponse response, Throwable cause) {
        super("%s (status %d): %s".formatted(message, response.statusCode(), response.body()), cause);
        this.statusCode = response.statusCode();
        this.responseBody = response.body();
    }

    public int statusCode() {
        return this.statusCode;
    }

    public String responseBody() {
        return this.responseBody;
    }
}
