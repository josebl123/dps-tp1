package edu.itba.class1.exchange.client.error;

import edu.itba.class1.exchange.http.HttpResponse;

public final class InvalidProviderRequestException extends CurrencyProviderException {
    public InvalidProviderRequestException(HttpResponse response) {
        super("Invalid request to currency provider", response);
    }
}
