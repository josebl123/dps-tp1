package edu.itba.class1.exchange.http;

public enum HttpStatus {
    OK(200),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    UNPROCESSABLE_CONTENT(422),
    TOO_MANY_REQUESTS(429);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }
}
