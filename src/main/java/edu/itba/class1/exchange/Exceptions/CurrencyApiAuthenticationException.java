package edu.itba.class1.exchange.Exceptions;

public class CurrencyApiAuthenticationException extends CurrencyRateProviderException {
    public CurrencyApiAuthenticationException() {
        super("La API rechazó las credenciales (revisá la apikey)");
    }
}