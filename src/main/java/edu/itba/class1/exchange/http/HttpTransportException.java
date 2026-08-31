package edu.itba.class1.exchange.http;

public final class HttpTransportException extends RuntimeException {
    public HttpTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
