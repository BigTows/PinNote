package org.bigtows.component.json;

import com.intellij.openapi.components.Service;

/**
 * Interface of json parser
 */
@Service
public interface JsonParser {

    /**
     * Parse content and get instance of T
     *
     * @param content json content
     * @param clazz   clazz
     * @param <T>     generic of class
     * @return instance of T
     */
    <T> T parse(String content, Class<T> clazz);

    /**
     * Create json-content by object
     *
     * @param object target object
     * @return json-content
     */
    String toJson(Object object);
}
