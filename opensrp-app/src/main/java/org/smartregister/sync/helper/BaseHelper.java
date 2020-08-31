package org.smartregister.sync.helper;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.smartregister.AllConstants;
import org.smartregister.domain.SyncProgress;

/**
 * Created by Richard Kareko on 6/4/20.
 */

public class BaseHelper {

    public void sendSyncProgressBroadcast(SyncProgress syncProgress, Context context) {
        Intent intent = new Intent();
        intent.setAction(AllConstants.SyncProgressConstants.ACTION_SYNC_PROGRESS);
        intent.putExtra(AllConstants.SyncProgressConstants.SYNC_PROGRESS_DATA, syncProgress);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
