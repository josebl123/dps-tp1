package edu.itba.class1.exchange.Api;

import java.net.URI;

public record CurrencyApiConfig(URI baseUrl, String apiKey) {
    private static final URI DEFAULT_BASE_URL = URI.create("https://api.freecurrencyapi.com");

    public static CurrencyApiConfig fromEnvironment() {
        var key = System.getenv("CURRENCY_API_KEY");
        if (key == null || key.isBlank()) throw new IllegalStateException("Missing CURRENCY_API_KEY");
        return new CurrencyApiConfig(DEFAULT_BASE_URL, key);
    }
}