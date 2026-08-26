package edu.itba.class1.exchange.Exceptions;
public class CurrencyApiUnavailableException extends CurrencyRateProviderException {
    public CurrencyApiUnavailableException(int statusCode) {
        super("La API no está disponible (status " + statusCode + ")");
    }
}