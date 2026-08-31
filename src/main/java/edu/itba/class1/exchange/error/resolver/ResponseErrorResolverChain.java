package edu.itba.class1.exchange.error.resolver;

import edu.itba.class1.exchange.http.HttpResponse;

import java.util.Collection;
import java.util.List;

public class ResponseErrorResolverChain {
    private final Collection<ResponseErrorResolver> resolvers;
    private final ResponseErrorResolver genericResponseErrorResolver;

    public ResponseErrorResolverChain(Collection<ResponseErrorResolver> resolvers) {
        this.resolvers = List.copyOf(resolvers);
        this.genericResponseErrorResolver = new GenericResponseErrorResolver();
    }

    public void resolve(HttpResponse response) {
        if (response.statusCode() == 200) {
            return;
        }

        this.resolvers.stream()
                .filter(resolver -> resolver.applyFor(response))
                .findFirst()
                .orElse(this.genericResponseErrorResolver)
                .resolve(response);
    }
}
