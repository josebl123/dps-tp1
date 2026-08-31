package edu.itba.class1.exchange.client.error;

public final class CurrencyProviderRateLimitException extends CurrencyProviderException {
    public CurrencyProviderRateLimitException() {
        super("Currency provider rate limit exceeded");
    }
}
