package edu.itba.class1.exchange.error.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.itba.class1.exchange.http.HttpResponse;
import edu.itba.class1.exchange.error.CurrencyApiException;
import edu.itba.class1.exchange.error.InvalidCredentialsException;
import edu.itba.class1.exchange.error.InvalidRequestException;
import edu.itba.class1.exchange.error.RateLimitExceededException;
import edu.itba.class1.exchange.error.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ResponseErrorResolverChainTest {
    private static final HttpResponse OK = new HttpResponse("success", 200);
    private static final HttpResponse ERROR = new HttpResponse("error-body", 503);

    @Test
    void doesNothingWhenNoResolverApplies() {
        var resolver = mock(ResponseErrorResolver.class);

        new ResponseErrorResolverChain(List.of(resolver)).resolve(OK);

        verify(resolver, never()).applyFor(OK);
        verify(resolver, never()).resolve(OK);
    }

    @Test
    void resolvesOnlyTheFirstApplicableResolver() {
        var first = mock(ResponseErrorResolver.class);
        var second = mock(ResponseErrorResolver.class);
        when(first.applyFor(ERROR)).thenReturn(true);
        when(second.applyFor(ERROR)).thenReturn(true);

        new ResponseErrorResolverChain(List.of(first, second)).resolve(ERROR);

        verify(first).resolve(ERROR);
        verify(second, never()).resolve(ERROR);
    }

    @ParameterizedTest
    @MethodSource("semanticResolvers")
    void semanticResolversApplyAndPreserveResponse(ResponseErrorResolver resolver,
                                                     Class<? extends CurrencyApiException> exceptionType,
                                                     int status) {
        var response = new HttpResponse("body-" + status, status);

        assertThat(resolver.applyFor(response)).isTrue();
        assertThatThrownBy(() -> resolver.resolve(response))
                .isInstanceOf(exceptionType)
                .satisfies(error -> {
                    var exception = (CurrencyApiException) error;
                    assertThat(exception.statusCode()).isEqualTo(status);
                    assertThat(exception.responseBody()).isEqualTo("body-" + status);
                    assertThat(exception.body()).isEqualTo("body-" + status);
                });
    }

    @Test
    void genericResolverHandlesUnknownAndServerErrors() {
        var resolver = new GenericResponseErrorResolver();

        assertThat(resolver.applyFor(new HttpResponse("", 500))).isTrue();
        assertThat(resolver.applyFor(new HttpResponse("", 418))).isTrue();
        assertThat(resolver.applyFor(OK)).isFalse();
        assertThatThrownBy(() -> resolver.resolve(ERROR))
                .isExactlyInstanceOf(CurrencyApiException.class)
                .satisfies(error -> {
                    var exception = (CurrencyApiException) error;
                    assertThat(exception.statusCode()).isEqualTo(503);
                    assertThat(exception.body()).isEqualTo("error-body");
                });
    }

    static Stream<Arguments> semanticResolvers() {
        return Stream.of(
                Arguments.of(new InvalidCredentialsResponseErrorResolver(), InvalidCredentialsException.class, 401),
                Arguments.of(new InvalidCredentialsResponseErrorResolver(), InvalidCredentialsException.class, 403),
                Arguments.of(new ResourceNotFoundResponseErrorResolver(), ResourceNotFoundException.class, 404),
                Arguments.of(new InvalidRequestResponseErrorResolver(), InvalidRequestException.class, 422),
                Arguments.of(new RateLimitResponseErrorResolver(), RateLimitExceededException.class, 429));
    }

}
