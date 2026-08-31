package edu.itba.class1.exchange.error.resolver;

import edu.itba.class1.exchange.error.ResourceNotFoundException;
import edu.itba.class1.exchange.http.HttpResponse;

public class ResourceNotFoundResponseErrorResolver implements ResponseErrorResolver {
    @Override
    public boolean applyFor(HttpResponse response) {
        return response.statusCode() == 404;
    }

    @Override
    public void resolve(HttpResponse response) {
        throw new ResourceNotFoundException(response.statusCode(), response.body());
    }
}
