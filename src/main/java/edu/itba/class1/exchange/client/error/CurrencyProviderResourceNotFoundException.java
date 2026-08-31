package edu.itba.class1.exchange.client.error;

public final class CurrencyProviderResourceNotFoundException extends CurrencyProviderException {
    public CurrencyProviderResourceNotFoundException() {
        super("Currency provider resource was not found");
    }
}
