package edu.itba.class1.exchange.client.currencyapi;

import edu.itba.class1.exchange.http.HttpResponse;

import java.util.Map;
import java.util.function.Supplier;

public final class ResponseStatusChecker {
    private final Map<Integer, Supplier<? extends RuntimeException>> errors;
    private final Supplier<? extends RuntimeException> defaultError;

    public ResponseStatusChecker(Map<Integer, Supplier<? extends RuntimeException>> errors,
                                 Supplier<? extends RuntimeException> defaultError) {
        this.errors = Map.copyOf(errors);
        this.defaultError = defaultError;
    }

    public void check(HttpResponse response) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return;
        }
        throw errors.getOrDefault(response.statusCode(), defaultError).get();
    }

}
