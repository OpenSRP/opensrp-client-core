package org.smartregister.view.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;

import static org.smartregister.util.Log.logError;

/**
 * Created by Jason Rogena - jrogena@ona.io on 18/05/2017.
 */

public class TimeChangedBroadcastReceiver extends BroadcastReceiver {
    private static TimeChangedBroadcastReceiver singleton;
    private final ArrayList<OnTimeChangedListener> onTimeChangedListeners;

    public TimeChangedBroadcastReceiver() {
        onTimeChangedListeners = new ArrayList<>();
    }

    public static void init(Context context) {
        if (singleton != null) {
            destroy(context);
        }

        singleton = new TimeChangedBroadcastReceiver();
        context.registerReceiver(singleton, new IntentFilter(Intent.ACTION_TIME_CHANGED));
        context.registerReceiver(singleton, new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED));
    }

    public static TimeChangedBroadcastReceiver getInstance() {
        return singleton;
    }

    public static void destroy(Context context) {
        try {
            if (singleton != null) {
                context.unregisterReceiver(singleton);
            }

        } catch (IllegalArgumentException e) {
            logError("Error on destroy: " + e);
        }
    }

    public void addOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        if (!onTimeChangedListeners.contains(onTimeChangedListener)) {
            onTimeChangedListeners.add(onTimeChangedListener);
        }
    }

    public void removeOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        if (onTimeChangedListeners.contains(onTimeChangedListener)) {
            onTimeChangedListeners.remove(onTimeChangedListener);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
            for (OnTimeChangedListener curListener : onTimeChangedListeners) {
                curListener.onTimeChanged();
            }
        } else if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
            for (OnTimeChangedListener curListener : onTimeChangedListeners) {
                curListener.onTimeZoneChanged();
            }
        }
    }

    public interface OnTimeChangedListener {
        void onTimeChanged();

        void onTimeZoneChanged();
    }
}
