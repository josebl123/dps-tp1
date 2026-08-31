package edu.itba.class1.exchange.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ResponseStatusCheckerTest {
    private final ResponseStatusChecker checker = new ResponseStatusChecker(
            Map.of(404, response -> new IllegalStateException("configured: " + response.statusCode())),
            response -> new IllegalStateException("default: " + response.statusCode()));

    @Test
    void acceptsAnySuccessfulStatus() {
        checker.check(new HttpResponse("", 200));
        checker.check(new HttpResponse("", 201));
        checker.check(new HttpResponse("", 204));
    }

    @Test
    void rejectsStatusCodesOutsideTheSuccessfulRange() {
        assertThatThrownBy(() -> checker.check(new HttpResponse("", 199)))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> checker.check(new HttpResponse("", 300)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void usesTheFactoryConfiguredForTheStatus() {
        assertThatThrownBy(() -> checker.check(new HttpResponse("", 404)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("configured: 404");
    }

    @Test
    void usesTheDefaultFactoryForUnknownStatuses() {
        assertThatThrownBy(() -> checker.check(new HttpResponse("", 500)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("default: 500");
    }

    @Test
    void passesTheFailedResponseToTheFactory() {
        var received = new HttpResponse[1];
        var checker = new ResponseStatusChecker(Map.of(), response -> {
            received[0] = response;
            return new IllegalStateException();
        });
        var failed = new HttpResponse("error-body", 503);

        assertThatThrownBy(() -> checker.check(failed)).isInstanceOf(IllegalStateException.class);
        assertThat(received[0]).isSameAs(failed);
    }

    @Test
    void createsAFreshExceptionForEachResponse() {
        var first = catchThrown();
        var second = catchThrown();

        assertThat(first).isNotSameAs(second);
    }

    private RuntimeException catchThrown() {
        try {
            checker.check(new HttpResponse("", 500));
            throw new AssertionError("expected the checker to throw");
        } catch (IllegalStateException exception) {
            return exception;
        }
    }
}
