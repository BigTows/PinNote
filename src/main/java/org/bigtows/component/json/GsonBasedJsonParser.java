package org.bigtows.component.json;

import com.google.gson.Gson;

/**
 * Implements of json parser.
 * Bassed on gson
 */
public class GsonBasedJsonParser implements JsonParser {

    /**
     * Instance of gson.
     */
    private Gson instance;

    /**
     * Constructor
     */
    public GsonBasedJsonParser() {
        this.instance = new Gson();
    }

    @Override
    public <T> T parse(String content, Class<T> clazz) {
        return this.instance.fromJson(content, clazz);
    }
    @Override
    public String toJson(Object object) {
        return this.instance.toJson(object);
    }

}
