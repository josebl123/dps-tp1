package edu.itba.class1.clients;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;

import edu.itba.class1.models.ExchangeRateResponse;
import edu.itba.class1.models.Money;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiClient implements IApiClient {

    private final String apiKey;

    @Override
    public BigDecimal get(Money money, Currency toCurrency) {
		ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse();
        try {
			final var response = Unirest.get("https://api.freecurrencyapi.com/v1/latest")
					.queryString("base_currency", money.currency()).queryString("currencies", toCurrency)
					.header("accept", "application/json")
					.header("apikey", apiKey).asJson();

			if (response.getStatus() != 200) {
				System.err.println("Error: " + response.getStatus());
			}

			exchangeRateResponse.setData(response.getBody().toString());
			return exchangeRateResponse.getExchange(toCurrency.getCurrencyCode());
		} catch (final Exception e) {
			return BigDecimal.ZERO;
		}
    }
    
}
