package org.smartregister.job;

import android.content.Intent;
import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.sync.intent.SettingsSyncIntentService;

/**
 * Created by ndegwamartin on 11/09/2018.
 */
public class SyncSettingsServiceJob extends BaseJob {

    public static final String TAG = "SyncSettingsServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), SettingsSyncIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;

    }
}
