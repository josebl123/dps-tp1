package edu.itba.class1.exchange.parser;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonJsonParser implements JsonParser {

    private final Gson gson = new Gson();

    @Override
    public <T> T parse(String json, Class<T> targetType) {
        final T parsed;
        try {
            parsed = gson.fromJson(json, targetType);
        } catch (JsonSyntaxException exception) {
            throw new JsonParseException(exception);
        }
        if (parsed == null) {
            throw new JsonParseException(new NullPointerException("Parsed object is null"));
        }
        return parsed;
    }
}
