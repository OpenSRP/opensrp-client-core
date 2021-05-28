package org.smartregister.sync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.job.P2pServiceJob;
import org.smartregister.p2p.callback.SyncFinishedCallback;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 10/05/2019
 */

public class P2PSyncFinishCallback implements SyncFinishedCallback {

    @Override
    public void onSuccess(@NonNull HashMap<String, Integer> hashMap) {
        scheduleProcessJob();
    }

    @Override
    public void onFailure(@NonNull Exception e, @Nullable HashMap<String, Integer> hashMap) {
        scheduleProcessJob();
    }

    private void scheduleProcessJob(){
        P2pServiceJob.scheduleJobImmediately(P2pServiceJob.TAG);
    }
}
