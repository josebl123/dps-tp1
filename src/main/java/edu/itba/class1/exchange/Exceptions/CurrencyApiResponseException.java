package edu.itba.class1.exchange.Exceptions;

public class CurrencyApiResponseException extends CurrencyRateProviderException {
  public CurrencyApiResponseException(String message) { super(message); }
  public CurrencyApiResponseException(String message, Throwable cause) { super(message, cause); }
}