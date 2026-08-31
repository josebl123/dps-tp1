package edu.itba.class1.exchange.client.error;

import edu.itba.class1.exchange.http.HttpResponse;

public final class CurrencyProviderRateLimitException extends CurrencyProviderException {
    public CurrencyProviderRateLimitException(HttpResponse response) {
        super("Currency provider rate limit exceeded", response);
    }
}
