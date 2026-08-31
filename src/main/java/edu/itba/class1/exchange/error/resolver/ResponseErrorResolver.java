package edu.itba.class1.exchange.error.resolver;

import edu.itba.class1.exchange.http.HttpResponse;

public interface ResponseErrorResolver {
    boolean applyFor(HttpResponse response);

    void resolve(HttpResponse response);
}
