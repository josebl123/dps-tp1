package edu.itba.class1.exchange.error.resolver;

import edu.itba.class1.exchange.error.InvalidCredentialsException;
import edu.itba.class1.exchange.http.HttpResponse;

import java.util.Set;

public class InvalidCredentialsResponseErrorResolver implements ResponseErrorResolver {
    private static final Set<Integer> STATUSES = Set.of(401, 403);

    @Override
    public boolean applyFor(HttpResponse response) {
        return STATUSES.contains(response.statusCode());
    }

    @Override
    public void resolve(HttpResponse response) {
        throw new InvalidCredentialsException(response.statusCode(), response.body());
    }
}
