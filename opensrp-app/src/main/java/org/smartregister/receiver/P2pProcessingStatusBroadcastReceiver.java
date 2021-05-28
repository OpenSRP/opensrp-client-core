package org.smartregister.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import org.smartregister.AllConstants;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-06-27
 */

public class P2pProcessingStatusBroadcastReceiver extends BroadcastReceiver {

    private StatusUpdate statusUpdate;

    public P2pProcessingStatusBroadcastReceiver(@NonNull StatusUpdate statusUpdate) {
        this.statusUpdate = statusUpdate;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(AllConstants.PeerToPeer.KEY_IS_PROCESSING) && statusUpdate != null) {
            statusUpdate.onStatusUpdate(intent.getBooleanExtra(AllConstants.PeerToPeer.KEY_IS_PROCESSING, false));
        }
    }

    public interface StatusUpdate {

        void onStatusUpdate(boolean isProcessing);
    }
}
