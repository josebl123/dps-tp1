package edu.itba.class1.exchange.parser;

import com.google.gson.Gson;
import edu.itba.class1.exchange.client.error.InvalidProviderResponseException;


public class GsonJsonParser implements JsonParser {

    private final Gson gson = new Gson();

    @Override
    public <T> T parse(String json, Class<T> targetType) {
        try {
            return gson.fromJson(json, targetType);
        } catch (RuntimeException exception) {
            throw new InvalidProviderResponseException(exception);
        }
    }
}
