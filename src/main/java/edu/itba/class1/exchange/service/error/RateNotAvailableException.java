package edu.itba.class1.exchange.service.error;

import java.time.LocalDate;
import java.util.Currency;

public final class RateNotAvailableException extends RuntimeException {
    public RateNotAvailableException(Currency from, Currency to) {
        super("No exchange rate available from %s to %s".formatted(from, to));
    }

    public RateNotAvailableException(Currency from, Currency to, LocalDate date) {
        super("No exchange rate available from %s to %s for %s".formatted(from, to, date));
    }
}
