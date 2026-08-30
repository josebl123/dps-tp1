package edu.itba.class1.exchange.parser;

public interface JsonParser {
    <T> T parse(String json, Class<T> targetType);
}
