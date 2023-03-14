package org.brainstorm.config.jsonhelper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class YesNoDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        String valueAsString = p.getValueAsString();

        return "YESyesYes".contains(String.valueOf(valueAsString)) ? Boolean.TRUE : Boolean.FALSE;
    }
}
