package edu.itba.class1.exchange.client.error;

public final class InvalidProviderResponseException extends CurrencyProviderException {
    public InvalidProviderResponseException(Throwable cause) {
        super("Currency provider returned an invalid response", cause);
    }
}
