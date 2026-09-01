package edu.itba.class1.exchange.parser;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonJsonParser implements JsonParser {

    private final Gson gson = new Gson();

    @Override
    public <T> T parse(String json, Class<T> targetType) {
        final Optional<T> parsed;
        try {
            parsed = Optional.of(gson.fromJson(json, targetType));
        } catch (JsonSyntaxException exception) {
            throw new JsonParseException(exception);
        }
        return parsed.orElseThrow(() -> new JsonParseException(new NullPointerException("Parsed object is null")));
    }
}
