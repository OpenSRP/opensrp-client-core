package org.smartregister.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.smartregister.domain.LocationProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by samuelgithengi on 11/30/18.
 */
public class PropertiesConverter implements JsonSerializer<LocationProperty>, JsonDeserializer<LocationProperty> {

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HHmm").create();

    @Override
    public JsonElement serialize(LocationProperty src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = gson.toJsonTree(src).getAsJsonObject();
        if (src.getCustomProperties() != null) {
            for (Map.Entry<String, String> entryset : src.getCustomProperties().entrySet()) {
                object.addProperty(entryset.getKey(), entryset.getValue());
            }
        }
        return object;
    }

    @Override
    public LocationProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        LocationProperty properties = gson.fromJson(json, LocationProperty.class);

        Set<Map.Entry<String, JsonElement>> entrySet = json.getAsJsonObject().entrySet();
        Set<String> mappedKeys = new HashSet<>();
        for (Field field : LocationProperty.class.getDeclaredFields()) {
            mappedKeys.add(field.getName());
        }
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            if (!mappedKeys.contains(entry.getKey())) {
                properties.getCustomProperties().put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        return properties;
    }
}
