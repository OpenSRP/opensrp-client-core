package org.smartregister.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalDate;

import java.lang.reflect.Type;

/**
 * Created by samuelgithengi on 11/22/18.
 */
public class DateTypeConverter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return new LocalDate(json.getAsString());
    }

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
