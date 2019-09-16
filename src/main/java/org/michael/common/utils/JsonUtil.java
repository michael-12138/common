package org.michael.common.utils;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created on 2019-09-16 11:31
 * Author : Michael.
 */
public final class JsonUtil {
    private JsonUtil() {
    }

    private static final ThreadLocal<ObjectMapper> object_mapper = new ThreadLocal<ObjectMapper>() {

        @Override
        protected ObjectMapper initialValue() {
            ObjectMapper jsonMapper = new ObjectMapper();
            jsonMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
            return jsonMapper;
        }
    };

    private static final ObjectMapper jsonMapperSync = new ObjectMapper();

    static {
        jsonMapperSync.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
    }

    public static <T> T jsonFromInput(InputStream in, String charset, Class<T> clazz) throws IOException, JsonProcessingException {
        Reader reader = new InputStreamReader(in, charset);
        return object_mapper.get().readValue(reader, clazz);
    }

    public static String jsonString(Object obj) throws IOException, JsonProcessingException {
        return object_mapper.get().writeValueAsString(obj);
    }

    public static String toJson(Object obj) {
        try {
            return jsonString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T jsonFrom(String str, Class<T> clazz) throws IOException, JsonProcessingException {
        return object_mapper.get().readValue(str, clazz);
    }
}
