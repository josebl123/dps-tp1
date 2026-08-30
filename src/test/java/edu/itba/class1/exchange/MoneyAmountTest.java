package edu.itba.class1.exchange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class MoneyAmountTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency JPY = Currency.getInstance("JPY");

    @Test
    void roundsAmountToTheCurrencyFractionDigits() {
        assertThat(new MoneyAmount(USD, new BigDecimal("10.126")).amount()).isEqualByComparingTo("10.13");
    }

    @Test
    void roundsAmountToWholeUnitsForCurrenciesWithoutFractionDigits() {
        assertThat(new MoneyAmount(JPY, new BigDecimal("14550.4")).amount()).isEqualByComparingTo("14550");
    }

    @Test
    void addsAmountsWithTheSameCurrency() {
        assertThat(new MoneyAmount(USD, new BigDecimal("10")).add(new MoneyAmount(USD, new BigDecimal("2.5"))))
                .isEqualTo(new MoneyAmount(USD, new BigDecimal("12.5")));
    }

    @Test
    void doesNotAddAmountsWithDifferentCurrencies() {
        assertThatThrownBy(() -> new MoneyAmount(USD, new BigDecimal("10")).add(new MoneyAmount(EUR, new BigDecimal("2"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot add amounts with different currencies");
    }

    @Test
    void multipliesByDecimalRate() {
        assertThat(new MoneyAmount(USD, new BigDecimal("10")).multiply(new BigDecimal("1.25")))
                .isEqualByComparingTo("12.50");
    }

    @Test
    void rejectsNullCurrencyAndAmount() {
        assertThatThrownBy(() -> new MoneyAmount(null, BigDecimal.ONE)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new MoneyAmount(USD, null)).isInstanceOf(IllegalArgumentException.class);
    }
}
