package org.smartregister.job;

import android.content.Intent;
import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.sync.intent.SyncLocationsByLevelAndTagsIntentService;

public class SyncLocationsByLevelAndTagsServiceJob extends BaseJob {

    public static final String TAG = "SyncLocationsByLevelAndTagsServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), SyncLocationsByLevelAndTagsIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
