package edu.itba.class1.exchange.client.error;

public final class AuthenticationFailedException extends CurrencyProviderException {
    public AuthenticationFailedException() {
        super("Currency provider authentication failed");
    }
}
