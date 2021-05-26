package org.smartregister.job;

import android.content.Intent;
import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.sync.intent.SyncLocationsByTeamIdsIntentService;

public class SyncLocationsByTeamIdsJob extends BaseJob {

    public static final String TAG = "SyncLocationsByTeamIdsJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), SyncLocationsByTeamIdsIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
