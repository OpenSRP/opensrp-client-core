package org.smartregister.job;

import android.content.Intent;
import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.sync.intent.SyncTaskIntentService;

public class SyncTaskServiceJob extends BaseJob {

    public static final String TAG = "SyncTaskServiceJob";


    private Class<? extends SyncTaskIntentService> serviceClass;

    public SyncTaskServiceJob(Class<? extends SyncTaskIntentService> serviceClass) {
        this.serviceClass = serviceClass;
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), serviceClass);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
