package edu.itba.class1.exchange.client;

import edu.itba.class1.exchange.client.error.AuthenticationFailedException;
import edu.itba.class1.exchange.client.error.CurrencyProviderException;
import edu.itba.class1.exchange.client.error.CurrencyProviderRateLimitException;
import edu.itba.class1.exchange.client.error.CurrencyProviderResourceNotFoundException;
import edu.itba.class1.exchange.client.error.InvalidProviderRequestException;
import edu.itba.class1.exchange.http.HttpResponse;

import java.util.Map;
import java.util.function.Supplier;

public final class ResponseStatusChecker {
    private final Map<Integer, Supplier<? extends RuntimeException>> errors;
    private final Supplier<? extends RuntimeException> defaultError;

    public ResponseStatusChecker(
            Map<Integer, Supplier<? extends RuntimeException>> errors,
            Supplier<? extends RuntimeException> defaultError) {
        this.errors = Map.copyOf(errors);
        this.defaultError = defaultError;
    }

    public void check(HttpResponse response) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return;
        }

        throw errors
                .getOrDefault(response.statusCode(), defaultError)
                .get();
    }

    public static ResponseStatusChecker forCurrencyProvider() {
        return new ResponseStatusChecker(
                Map.of(
                        401, AuthenticationFailedException::new,
                        403, AuthenticationFailedException::new,
                        404, CurrencyProviderResourceNotFoundException::new,
                        422, InvalidProviderRequestException::new,
                        429, CurrencyProviderRateLimitException::new),
                () -> new CurrencyProviderException("Currency provider request failed"));
    }
}
