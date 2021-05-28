package org.smartregister.util;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by koros on 3/24/16.
 */
public class AssetHandler {

    public static String readFileFromAssetsFolder(String fileName, Context context) {
        String fileContents = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Timber.e(ex);
            return null;
        }
        //Log.d("File", fileContents);
        return fileContents;
    }

    public static <T> T assetJsonToJava(Map<String, Object> jsonMap, Context context, String fileName, Class<T> clazz, Type type) {
        try {
            if (clazz == null) {
                return null;
            }

            if (jsonMap == null) {
                return null;
            } else if (jsonMap.containsKey(fileName)) {
                Object o = jsonMap.get(fileName);
                if (clazz.isAssignableFrom(o.getClass())) {
                    return clazz.cast(o);
                } else {
                    return null;
                }
            }

            String jsonString = readFileFromAssetsFolder(fileName, context);
            if (StringUtils.isBlank(jsonString)) {
                return null;
            }

            T t;
            if (type == null) {
                t = jsonStringToJava(jsonString, clazz);
            } else {
                t = jsonStringToJava(jsonString, type);
            }

            if (t != null) {
                jsonMap.put(fileName, t);
            }
            return t;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public static <T> T assetJsonToJava(Map<String, Object> jsonMap, Context context, String fileName, Class<T> clazz) {
        return assetJsonToJava(jsonMap, context, fileName, clazz, null);
    }

    public static <T> T jsonStringToJava(String jsonString, Class<T> clazz) {
        try {
            return JsonFormUtils.gson.fromJson(jsonString, clazz);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public static <T> T jsonStringToJava(String jsonString, Type type) {
        try {
            return JsonFormUtils.gson.fromJson(jsonString, type);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public static <T> String javaToJsonString(T t) {
        return javaToJsonString(t, null);
    }

    public static <T> String javaToJsonString(T t, Type type) {
        try {
            String s;
            if (type == null)
                s = JsonFormUtils.gson.toJson(t);
            else
                s = JsonFormUtils.gson.toJson(t, type);
            return s;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }


}
