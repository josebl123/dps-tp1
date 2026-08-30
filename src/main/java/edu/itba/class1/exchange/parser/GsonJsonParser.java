package edu.itba.class1.exchange.parser;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GsonJsonParser implements JsonParser {

    private final Gson gson;

    @Override
    public <T> T parse(String json, Class<T> targetType) {
        return gson.fromJson(json, targetType);
    }
}
