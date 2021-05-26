package org.smartregister.util;

public class FloatUtil {
    public static Float tryParse(String value, Float defaultValue) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String tryParse(String value, String defaultValue) {
        try {
            return String.valueOf(Float.parseFloat(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
