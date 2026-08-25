package edu.itba.class1.exchange.http;



import java.net.URI;
import java.util.Map;

public interface HttpClient {
	HttpResponse get(final URI url, Map<String, Object> queryParams, Map<String, String> headers);
}
