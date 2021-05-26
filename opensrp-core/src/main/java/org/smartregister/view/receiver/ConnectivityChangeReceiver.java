package org.smartregister.view.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.smartregister.sync.DrishtiSyncScheduler;

import static org.smartregister.util.Log.logInfo;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        logInfo("Connectivity change receiver triggered.");
        if (intent.getExtras() != null) {
            if (isDeviceDisconnectedFromNetwork(intent)) {
                logInfo("Device got disconnected from network. Stopping Dristhi Sync scheduler.");
                DrishtiSyncScheduler.stop(context);
                return;
            }
            if (isDeviceConnectedToNetwork(intent)) {
                logInfo("Device got connected to network. Trying to start Dristhi Sync scheduler.");
                DrishtiSyncScheduler.start(context);
            }
        }
    }

    private boolean isDeviceDisconnectedFromNetwork(Intent intent) {
        return intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
    }

    private boolean isDeviceConnectedToNetwork(Intent intent) {
        NetworkInfo networkInfo = (NetworkInfo) intent.getExtras()
                .get(ConnectivityManager.EXTRA_NETWORK_INFO);
        return networkInfo != null && networkInfo.isConnected();
    }
}

