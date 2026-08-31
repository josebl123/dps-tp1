package edu.itba.class1.exchange.http;

import java.util.Map;
import java.util.function.Function;

public final class ResponseStatusChecker {
    private final Map<Integer, Function<HttpResponse, ? extends RuntimeException>> errors;
    private final Function<HttpResponse, ? extends RuntimeException> defaultError;

    public ResponseStatusChecker(Map<Integer, Function<HttpResponse, ? extends RuntimeException>> errors,
                                 Function<HttpResponse, ? extends RuntimeException> defaultError) {
        this.errors = Map.copyOf(errors);
        this.defaultError = defaultError;
    }

    public void check(HttpResponse response) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return;
        }
        throw errors.getOrDefault(response.statusCode(), defaultError).apply(response);
    }
}
