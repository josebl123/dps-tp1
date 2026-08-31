package edu.itba.class1.exchange.client.error;

import edu.itba.class1.exchange.http.HttpResponse;

public final class CurrencyProviderResourceNotFoundException extends CurrencyProviderException {
    public CurrencyProviderResourceNotFoundException(HttpResponse response) {
        super("Currency provider resource not found", response);
    }
}
