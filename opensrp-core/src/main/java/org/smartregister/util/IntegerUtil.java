package org.smartregister.util;

public class IntegerUtil {
    public static Integer tryParse(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String tryParse(String value, String defaultValue) {
        try {
            return String.valueOf(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int compare(int lhs, int rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }
}
