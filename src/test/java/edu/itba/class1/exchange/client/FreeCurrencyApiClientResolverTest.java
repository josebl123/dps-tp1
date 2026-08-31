package edu.itba.class1.exchange.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.http.HttpResponse;
import edu.itba.class1.exchange.parser.JsonParser;
import edu.itba.class1.exchange.error.CurrencyApiException;
import edu.itba.class1.exchange.error.resolver.ResponseErrorResolverChain;
import edu.itba.class1.exchange.model.AvailableCurrenciesResponse;
import edu.itba.class1.exchange.model.ExchangeRateResponse;

import java.util.Currency;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class FreeCurrencyApiClientResolverTest {
    private final HttpClient httpClient = mock(HttpClient.class);
    private final JsonParser jsonParser = mock(JsonParser.class);
    private final ResponseErrorResolverChain chain = mock(ResponseErrorResolverChain.class);
    private final FreeCurrencyApiClient client = new FreeCurrencyApiClient(httpClient, jsonParser, chain);

    @Test
    void delegatesToChainBeforeParsingSuccessfulResponse() {
        var response = new HttpResponse("{}", 200);
        var expected = mock(ExchangeRateResponse.class);
        when(httpClient.get(any(), any(), any())).thenReturn(response);
        when(jsonParser.parse("{}", ExchangeRateResponse.class)).thenReturn(expected);

        var actual = client.getCurrencyRate(Currency.getInstance("USD"), Currency.getInstance("EUR"));

        assertThat(actual).isSameAs(expected);
        InOrder order = inOrder(chain, jsonParser);
        order.verify(chain).resolve(response);
        order.verify(jsonParser).parse("{}", ExchangeRateResponse.class);
    }

    @Test
    void chainExceptionPreventsParsing() {
        var response = new HttpResponse("bad", 500);
        when(httpClient.get(any(), any(), any())).thenReturn(response);
        var exception = new CurrencyApiException(500, "bad");
        org.mockito.Mockito.doThrow(exception).when(chain).resolve(response);

        org.junit.jupiter.api.Assertions.assertThrows(CurrencyApiException.class,
                client::getAvailableCurrencies);

        org.mockito.Mockito.verify(jsonParser, org.mockito.Mockito.never())
                .parse(any(), eq(AvailableCurrenciesResponse.class));
    }
}
