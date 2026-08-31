package edu.itba.class1.exchange.client.error;

import edu.itba.class1.exchange.http.HttpResponse;

public final class InvalidProviderResponseException extends CurrencyProviderException {
    public InvalidProviderResponseException(HttpResponse response, Throwable cause) {
        super("Currency provider returned an invalid response", response, cause);
    }
}
