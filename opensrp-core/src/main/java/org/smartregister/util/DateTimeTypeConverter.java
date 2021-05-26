package org.smartregister.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;

public class DateTimeTypeConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

    private DateTimeFormatter dateTimeFormatter;

    public DateTimeTypeConverter() {
    }

    public DateTimeTypeConverter(String dateTimeFormat) {
        dateTimeFormatter = DateTimeFormat.forPattern(dateTimeFormat);
    }

    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) {
            return null;
        } else if (json.isJsonObject()) {
            JsonObject je = json.getAsJsonObject();
            return new DateTime(je.get("iMillis").getAsLong());
        } else if (json.isJsonPrimitive() && dateTimeFormatter == null) {
            return new DateTime(json.getAsString());
        } else if (json.isJsonPrimitive() && dateTimeFormatter != null) {
            try {
                return dateTimeFormatter.parseDateTime(json.getAsString());
            } catch (IllegalArgumentException e) {
                return new DateTime(json.getAsString());
            }
        } else return null;
    }

    @Override
    public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
        if (dateTimeFormatter == null)
            return new JsonPrimitive(src.toString());
        else
            return new JsonPrimitive(src.toString(dateTimeFormatter));
    }
}