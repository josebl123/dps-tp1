package edu.itba.class1.exchange.Exceptions;

public class CurrencyApiConnectionException extends CurrencyRateProviderException {
    public CurrencyApiConnectionException(Throwable cause) {
        super("No se pudo conectar con la API", cause);
    }
}
