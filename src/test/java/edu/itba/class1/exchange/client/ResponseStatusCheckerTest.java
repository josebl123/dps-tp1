package edu.itba.class1.exchange.client.currencyapi;

import edu.itba.class1.exchange.client.error.AuthenticationFailedException;
import edu.itba.class1.exchange.client.error.CurrencyProviderException;
import edu.itba.class1.exchange.client.error.CurrencyProviderRateLimitException;
import edu.itba.class1.exchange.client.error.CurrencyProviderResourceNotFoundException;
import edu.itba.class1.exchange.client.error.InvalidProviderRequestException;
import edu.itba.class1.exchange.http.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResponseStatusCheckerTest {
    private final ResponseStatusChecker checker = new ResponseStatusChecker(
            Map.of(
                    401, AuthenticationFailedException::new,
                    403, AuthenticationFailedException::new,
                    404, CurrencyProviderResourceNotFoundException::new,
                    422, InvalidProviderRequestException::new,
                    429, CurrencyProviderRateLimitException::new),
            () -> new CurrencyProviderException("Currency provider request failed"));

    @Test
    void acceptsAnySuccessfulStatus() {
        checker.check(new HttpResponse("", 201));
        checker.check(new HttpResponse("", 204));
    }

    @Test
    void mapsAuthenticationStatuses() {
        assertThatThrownBy(() -> checker.check(new HttpResponse("", 401)))
                .isExactlyInstanceOf(AuthenticationFailedException.class);
        assertThatThrownBy(() -> checker.check(new HttpResponse("", 403)))
                .isExactlyInstanceOf(AuthenticationFailedException.class);
    }

    @Test
    void mapsRateLimitStatus() {
        assertThatThrownBy(() -> checker.check(new HttpResponse("", 429)))
                .isExactlyInstanceOf(CurrencyProviderRateLimitException.class);
    }

    @Test
    void mapsResourceAndRequestStatuses() {
        assertThatThrownBy(() -> checker.check(new HttpResponse("", 404)))
                .isExactlyInstanceOf(CurrencyProviderResourceNotFoundException.class);
        assertThatThrownBy(() -> checker.check(new HttpResponse("", 422)))
                .isExactlyInstanceOf(InvalidProviderRequestException.class);
    }

    @Test
    void usesDefaultErrorForUnknownStatuses() {
        assertThatThrownBy(() -> checker.check(new HttpResponse("", 500)))
                .isExactlyInstanceOf(CurrencyProviderException.class);
    }

    @Test
    void createsFreshExceptionsForEachResponse() {
        var checker = new ResponseStatusChecker(
                Map.of(500, () -> new CurrencyProviderException("provider failure")),
                () -> new CurrencyProviderException("provider failure"));

        var first = org.junit.jupiter.api.Assertions.assertThrows(
                CurrencyProviderException.class,
                () -> checker.check(new HttpResponse("", 500)));
        var second = org.junit.jupiter.api.Assertions.assertThrows(
                CurrencyProviderException.class,
                () -> checker.check(new HttpResponse("", 500)));

        org.assertj.core.api.Assertions.assertThat(first).isNotSameAs(second);
    }
}
