package org.smartregister.job;

import android.content.Intent;
import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.sync.intent.SyncIntentService;

/**
 * Created by ndegwamartin on 05/09/2018.
 */
public class SyncServiceJob extends BaseJob {

    public static final String TAG = "SyncServiceJob";

    private Class<? extends SyncIntentService> serviceClass;

    public SyncServiceJob(Class<? extends SyncIntentService> serviceClass) {
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
