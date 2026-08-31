package edu.itba.class1.exchange.client.error;

import edu.itba.class1.exchange.http.HttpResponse;

public final class AuthenticationFailedException extends CurrencyProviderException {
    public AuthenticationFailedException(HttpResponse response) {
        super("Currency provider authentication failed", response);
    }
}
