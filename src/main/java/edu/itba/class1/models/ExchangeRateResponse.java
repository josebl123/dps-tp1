package edu.itba.class1.models;

import java.math.BigDecimal;
import java.util.Map;

import com.google.gson.Gson;

public class ExchangeRateResponse {
	private Map<String, BigDecimal> data;

	public void setData(String response) {
		this.data = new Gson().fromJson(response, ExchangeRateResponse.class).data;
	}

	public BigDecimal getExchange(final String toCurrency) {
		return this.data.get(toCurrency);
	}
}