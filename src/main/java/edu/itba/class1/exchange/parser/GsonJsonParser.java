package edu.itba.class1.exchange.parser;

import com.google.gson.Gson;


public class GsonJsonParser implements JsonParser {

    private final Gson gson = new Gson();

    @Override
    public <T> T parse(String json, Class<T> targetType) {
        return gson.fromJson(json, targetType);
    }
}
