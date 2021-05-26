package org.smartregister.job;

import android.content.Intent;
import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.sync.intent.DocumentConfigurationIntentService;

/**
 * Created by cozej4 on 2020-04-08.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class DocumentConfigurationServiceJob extends BaseJob {
    public static final String TAG = "DocumentConfigurationServiceJob";

    private Class<? extends DocumentConfigurationIntentService> serviceClass;

    public DocumentConfigurationServiceJob(Class<? extends DocumentConfigurationIntentService> serviceClass) {
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
