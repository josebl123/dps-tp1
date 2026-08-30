package edu.itba.class1.exchange.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edu.itba.class1.exchange.ExchangeRateResponse;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

class GsonJsonParserTest {
    private final JsonParser parser = new GsonJsonParser(new Gson());

    @Test
    void parsesJsonIntoTheRequestedType() {
        var response = parser.parse("{\"data\":{\"EUR\":{\"code\":\"EUR\",\"value\":0.92}}}", ExchangeRateResponse.class);

        assertThat(response.getExchange("EUR")).isEqualTo(0.92);
    }

    @Test
    void rejectsMalformedJson() {
        assertThatThrownBy(() -> parser.parse("{malformed", ExchangeRateResponse.class))
                .isInstanceOf(JsonSyntaxException.class);
    }
}
