package org.smartregister.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.AllConstants;
import org.smartregister.domain.SyncProgress;

import java.io.Serializable;

/**
 * Created by Richard Kareko on 6/4/20.
 */

public class SyncProgressBroadcastReceiver extends BroadcastReceiver {

    private final SyncProgressListener syncProgressListener;

    public SyncProgressBroadcastReceiver(SyncProgressListener syncProgressListener) {
        this.syncProgressListener = syncProgressListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        if (data != null) {
            Serializable syncProgressDataSerializable = data.getSerializable(AllConstants.SyncProgressConstants.SYNC_PROGRESS_DATA);
            if (syncProgressDataSerializable instanceof SyncProgress) {
                SyncProgress syncProgress = (SyncProgress) syncProgressDataSerializable;
                syncProgressListener.onSyncProgress(syncProgress);
            }
        }
    }

    public interface SyncProgressListener {
        void onSyncProgress(SyncProgress syncProgress);
    }
}
