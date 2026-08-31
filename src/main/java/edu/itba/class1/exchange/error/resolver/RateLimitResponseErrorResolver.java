package edu.itba.class1.exchange.error.resolver;

import edu.itba.class1.exchange.error.RateLimitExceededException;
import edu.itba.class1.exchange.http.HttpResponse;

public class RateLimitResponseErrorResolver implements ResponseErrorResolver {
    @Override
    public boolean applyFor(HttpResponse response) {
        return response.statusCode() == 429;
    }

    @Override
    public void resolve(HttpResponse response) {
        throw new RateLimitExceededException(response.statusCode(), response.body());
    }
}
