package edu.itba.class1.exchange.Api;

import java.net.URI;

public class CurrencyApiEndpoints {
    private final URI baseUrl;
    public CurrencyApiEndpoints(URI baseUrl) {
        this.baseUrl = baseUrl;
    }
    public URI latestRates(){
        return baseUrl.resolve("/v1/latest");
    }
    public URI historicalRates(){
        return baseUrl.resolve("/v1/historical");
    }
    public URI supportedCurrencies()  {
        return baseUrl.resolve("/v1/currencies");
    }
}