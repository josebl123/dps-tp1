package edu.itba.class1.exchange.error.resolver;

import edu.itba.class1.exchange.error.CurrencyApiException;
import edu.itba.class1.exchange.http.HttpResponse;

public class GenericResponseErrorResolver implements ResponseErrorResolver {
    @Override
    public boolean applyFor(HttpResponse response) {
        return response.statusCode() != 200;
    }

    @Override
    public void resolve(HttpResponse response) {
        throw new CurrencyApiException(response.statusCode(), response.body());
    }
}
