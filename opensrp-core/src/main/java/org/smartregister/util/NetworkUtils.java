package org.smartregister.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.smartregister.CoreLibrary;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 15/03/2018.
 */

public class NetworkUtils {

    public static boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) CoreLibrary.getInstance().context().applicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        } catch (Exception e) {
            Timber.e(e);

        }

        return false;
    }
}
