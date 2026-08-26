package edu.itba.class1.exchange.Api;

import edu.itba.class1.exchange.Exceptions.*;
import edu.itba.class1.exchange.http.HttpResponse;

public class CurrencyApiErrorMapper {
    public CurrencyRateProviderException toDomainException(HttpResponse response) {
        return switch (response.statusCode()) {
            case 401, 403 -> new CurrencyApiAuthenticationException();
            case 422      -> new CurrencyApiResponseException("Invalid request: " + response.body());
            case 429      -> new CurrencyApiRateLimitExceededException();
            default       -> response.statusCode() >= 500
                    ? new CurrencyApiUnavailableException(response.statusCode())
                    : new CurrencyApiResponseException("Unexpected status " + response.statusCode() + ": " + response.body());
        };
    }
}
