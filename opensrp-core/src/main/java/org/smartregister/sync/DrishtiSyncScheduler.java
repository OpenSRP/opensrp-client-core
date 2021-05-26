package org.smartregister.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.smartregister.CoreLibrary;
import org.smartregister.event.Listener;

import static java.text.MessageFormat.format;
import static org.joda.time.DateTimeConstants.MILLIS_PER_MINUTE;
import static org.joda.time.DateTimeConstants.MILLIS_PER_SECOND;
import static org.smartregister.event.Event.ON_LOGOUT;
import static org.smartregister.util.Log.logInfo;

public class DrishtiSyncScheduler {
    public static final int SYNC_INTERVAL = 2 * MILLIS_PER_MINUTE;
    public static final int SYNC_START_DELAY = 5 * MILLIS_PER_SECOND;
    private static Listener<Boolean> logoutListener;
    private static Object ReceiverClass;

    public static void setReceiverClass(Class receiverClass) {
        ReceiverClass = receiverClass;
    }

    public static void start(final Context context) {
        if (CoreLibrary.getInstance().context().IsUserLoggedOut()) {
            return;
        }

        if (ReceiverClass == null) {
            return;
        }

        PendingIntent syncBroadcastReceiverIntent = PendingIntent
                .getBroadcast(context, 0, new Intent(context, (Class) ReceiverClass), 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + SYNC_START_DELAY,
                SYNC_INTERVAL, syncBroadcastReceiverIntent);

        logInfo(format("Scheduled to sync from server every {0} seconds.", SYNC_INTERVAL / 1000));

        attachListenerToStopSyncOnLogout(context);
    }

    private static void attachListenerToStopSyncOnLogout(final Context context) {
        ON_LOGOUT.removeListener(logoutListener);
        logoutListener = new Listener<Boolean>() {
            public void onEvent(Boolean data) {
                logInfo("User is logged out. Stopping Dristhi Sync scheduler.");
                stop(context);
            }
        };
        ON_LOGOUT.addListener(logoutListener);
    }

    public static void startOnlyIfConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            start(context);
        } else {
            logInfo("Device not connected to network so not starting sync scheduler.");
        }
    }

    public static void stop(Context context) {
        if (ReceiverClass == null) {
            return;
        }

        PendingIntent syncBroadcastReceiverIntent = PendingIntent
                .getBroadcast(context, 0, new Intent(context, (Class) ReceiverClass), 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(syncBroadcastReceiverIntent);

        logInfo("Unscheduled sync.");
    }
}
