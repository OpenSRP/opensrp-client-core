package org.smartregister.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Jason Rogena - jrogena@ona.io on 30/03/2017.
 */

public class DisplayUtils {
    public enum ScreenDpi {
        LDPI,
        MDPI,
        HDPI,
        XHDPI,
        XXHDPI,
        XXXHDPI
    }

    public static ScreenDpi getScreenDpi(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        ScreenDpi dpi = ScreenDpi.XXXHDPI;

        if (density <= 0.75) {
            dpi = ScreenDpi.LDPI;
        } else if (density > 0.75 && density <= 1.0) {
            dpi = ScreenDpi.MDPI;
        } else if (density > 1.0 && density <= 1.5) {
            dpi = ScreenDpi.HDPI;
        } else if (density > 1.5 && density <= 2.0) {
            dpi = ScreenDpi.XHDPI;
        } else if (density > 2.0 && density <= 3.0) {
            dpi = ScreenDpi.XXHDPI;
        }

        return dpi;
    }

    public static int getDisplayWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;
    }

    public static int getDisplayHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.heightPixels;
    }

    public static double getScreenSize(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);

        return Math.sqrt(x + y);
    }
}
