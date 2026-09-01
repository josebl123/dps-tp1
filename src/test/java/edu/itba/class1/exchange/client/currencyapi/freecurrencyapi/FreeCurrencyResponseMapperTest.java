package edu.itba.class1.exchange.client.currencyapi.freecurrencyapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response.AvailableCurrenciesResponse;
import edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response.ExchangeRateResponse;
import edu.itba.class1.exchange.client.currencyapi.freecurrencyapi.response.HistoricalExchangeRateResponse;
import edu.itba.class1.exchange.parser.GsonJsonParser;
import edu.itba.class1.exchange.service.error.RateNotAvailableException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Test;

class FreeCurrencyResponseMapperTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");

    private final FreeCurrencyResponseMapper mapper = new FreeCurrencyResponseMapper();
    private final GsonJsonParser parser = new GsonJsonParser();

    @Test
    void mapsLatestResponseToDomainRates() {
        var response = parser.parse("{\"data\":{\"EUR\":0.918456}}", ExchangeRateResponse.class);

        var rates = mapper.toRates(USD, List.of(EUR), response);

        assertThat(rates).singleElement().satisfies(rate -> {
            assertThat(rate.fromCurrency()).isEqualTo(USD);
            assertThat(rate.toCurrency()).isEqualTo(EUR);
            assertThat(rate.rate()).isEqualByComparingTo("0.918456");
        });
    }

    @Test
    void mapsHistoricalResponseToDomainRates() {
        var date = LocalDate.of(2024, 11, 20);
        var response = parser.parse("{\"data\":{\"2024-11-20\":{\"EUR\":0.879908}}}", HistoricalExchangeRateResponse.class);

        var rates = mapper.toHistoricalRates(USD, List.of(EUR), date, response);

        assertThat(rates).singleElement().satisfies(rate -> {
            assertThat(rate.rate()).isEqualByComparingTo("0.879908");
            assertThat(rate.timestamp()).isEqualTo(Instant.parse("2024-11-20T00:00:00Z"));
        });
    }

    @Test
    void mapsAvailableCurrencies() {
        var response = parser.parse("{\"data\":{\"USD\":{\"code\":\"USD\"},\"EUR\":{\"code\":\"EUR\"}}}", AvailableCurrenciesResponse.class);

        assertThat(mapper.toCurrencies(response)).containsExactly(USD, EUR);
    }

    @Test
    void reportsWhenTheLatestResponseDoesNotContainARequestedRate() {
        var response = parser.parse("{\"data\":{}}", ExchangeRateResponse.class);

        assertThatThrownBy(() -> mapper.toRates(USD, List.of(EUR), response))
                .isExactlyInstanceOf(RateNotAvailableException.class)
                .hasMessage("No exchange rate available from USD to EUR");
    }

    @Test
    void reportsWhenTheHistoricalResponseDoesNotContainARequestedRate() {
        var date = LocalDate.of(2024, 11, 20);
        var response = parser.parse("{\"data\":{\"2024-11-20\":{}}}", HistoricalExchangeRateResponse.class);

        assertThatThrownBy(() -> mapper.toHistoricalRates(USD, List.of(EUR), date, response))
                .isExactlyInstanceOf(RateNotAvailableException.class)
                .hasMessage("No exchange rate available from USD to EUR for 2024-11-20");
    }
}
