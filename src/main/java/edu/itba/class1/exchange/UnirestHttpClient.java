package edu.itba.class1.exchange;



import com.mashape.unirest.http.Unirest;

import edu.itba.class1.exchange.http.HttpClient;
import edu.itba.class1.exchange.http.HttpResponse;

import java.net.URI;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnirestHttpClient implements HttpClient {

	private static final Logger LOGGER = LogManager.getLogger(UnirestHttpClient.class);

	@Override
	public HttpResponse get(final URI url, final Map<String, Object> queryParams,
			final Map<String, String> headers) {
		try {
			final var response = Unirest.get(url.toString()).queryString(queryParams).headers(headers).asJson();
			return new HttpResponse(response.getBody().toString(), response.getStatus());
		} catch (final Exception e) {
			LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
			return new HttpResponse("{\"error\":\"Internal Server Error\"}", HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
