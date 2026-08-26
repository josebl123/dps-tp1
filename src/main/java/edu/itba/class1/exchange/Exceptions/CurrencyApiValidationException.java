package edu.itba.class1.exchange.Exceptions;


public class CurrencyApiValidationException extends CurrencyRateProviderException {
  public CurrencyApiValidationException(String details) {
    super("La API rechazó el pedido por inválido: " + details);
  }
}