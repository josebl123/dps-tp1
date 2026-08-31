package edu.itba.class1.exchange.client.error;

public final class InvalidProviderRequestException extends CurrencyProviderException {
    public InvalidProviderRequestException() {
        super("Currency provider rejected the request");
    }
}
