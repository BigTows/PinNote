package org.bigtows.component.json;

import com.intellij.openapi.components.Service;

@Service
public interface JsonParser {


    <T> T parse(String content, Class<T> clazz);


    String toJson(Object object);
}
