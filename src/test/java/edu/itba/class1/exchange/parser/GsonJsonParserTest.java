package edu.itba.class1.exchange.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response.ExchangeRateResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class GsonJsonParserTest {
    private final JsonParser parser = new GsonJsonParser();

    @Test
    void parsesJsonIntoTheRequestedType() {
        var response = parser.parse("{\"data\":{\"EUR\":0.92}}", ExchangeRateResponse.class);

        assertThat(response.findExchange("EUR")).hasValue(new BigDecimal("0.92"));
    }

    @Test
    void rejectsMalformedJson() {
        assertThatThrownBy(() -> parser.parse("{malformed", ExchangeRateResponse.class))
                .isInstanceOf(JsonParseException.class);
    }

    @Test
    void rejectsAnEmptyResponseBody() {
        assertThatThrownBy(() -> parser.parse("", ExchangeRateResponse.class))
                .isInstanceOf(JsonParseException.class);
    }

    @Test
    void rejectsAJsonNullLiteral() {
        assertThatThrownBy(() -> parser.parse("null", ExchangeRateResponse.class))
                .isInstanceOf(JsonParseException.class);
    }
}
