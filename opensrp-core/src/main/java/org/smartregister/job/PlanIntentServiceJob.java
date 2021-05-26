package org.smartregister.job;

import android.content.Intent;
import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.sync.intent.PlanIntentService;

/**
 * Created by Vincent Karuri on 08/05/2019
 */
public class PlanIntentServiceJob extends BaseJob {
    public static final String TAG = "PlanIntenætServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), PlanIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
