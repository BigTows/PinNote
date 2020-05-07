package org.bigtows.component.json;

import com.google.gson.Gson;

public class GoonBasedJsonParser implements JsonParser {

    private Gson instance;

    public GoonBasedJsonParser() {
        this.instance = new Gson();
    }

    public <T> T parse(String content, Class<T> clazz) {
        return this.instance.fromJson(content, clazz);
    }

    public String toJson(Object object) {
        return this.instance.toJson(object);
    }

}
