package org.smartregister.util;

import static android.util.Log.d;
import static android.util.Log.e;
import static android.util.Log.i;
import static android.util.Log.v;
import static android.util.Log.w;

public class Log {
    public static void logVerbose(String message) {
        v("DRISHTI", message);
    }

    public static void logInfo(String message) {
        i("DRISHTI", message);
    }

    public static void logDebug(String message) {
        d("DRISHTI", message);
    }

    public static void logWarn(String message) {
        w("DRISHTI", message);
    }

    public static void logError(String message) {
        e("DRISHTI", message);
    }

    public static void logError(String tag, String message) {
        e(tag, message);
    }

}
