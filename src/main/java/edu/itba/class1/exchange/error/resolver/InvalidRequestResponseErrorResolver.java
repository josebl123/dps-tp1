package edu.itba.class1.exchange.error.resolver;

import edu.itba.class1.exchange.error.InvalidRequestException;
import edu.itba.class1.exchange.http.HttpResponse;

public class InvalidRequestResponseErrorResolver implements ResponseErrorResolver {
    @Override
    public boolean applyFor(HttpResponse response) {
        return response.statusCode() == 422;
    }

    @Override
    public void resolve(HttpResponse response) {
        throw new InvalidRequestException(response.statusCode(), response.body());
    }
}
