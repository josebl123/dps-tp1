package edu.itba.class1.exchange.http;

public record HttpResponse(String body, int statusCode) {
    public boolean isSuccessful() {
        return this.statusCode >= 200 && this.statusCode < 300;
    }
}
