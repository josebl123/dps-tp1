package edu.itba.class1.exchange.parser;

public final class JsonParseException extends RuntimeException {
    public JsonParseException(Throwable cause) {
        super("Could not parse JSON response", cause);
    }
}
