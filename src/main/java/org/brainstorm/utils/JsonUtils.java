package org.brainstorm.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.json.JsonParseException;

import java.util.Map;

public class JsonUtils {

    public static <T> T convertJsonToJava(Map<String,Object> map, Class<T> cls) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T result = null;
        try {
            result = mapper.convertValue(map,cls);
        } catch (JsonParseException e) {
            return result;
        }
        return result;
    }
}
