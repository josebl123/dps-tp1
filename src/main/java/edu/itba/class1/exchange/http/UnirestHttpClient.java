package edu.itba.class1.exchange.http;



import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


import java.net.URI;
import java.util.Map;

public class UnirestHttpClient implements HttpClient {
	@Override
	public HttpResponse get(final URI url, final Map<String, Object> queryParams,
			final Map<String, String> headers) {
		try {
			final var response = Unirest.get(url.toString()).queryString(queryParams).headers(headers).asJson();
			return new HttpResponse(response.getBody().toString(), response.getStatus());
		}  catch (final UnirestException e) {
		throw new HttpTransportException("Could not reach currency provider", e);
	}
	}
}
