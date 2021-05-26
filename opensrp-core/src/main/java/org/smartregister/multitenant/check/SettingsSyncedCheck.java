package org.smartregister.multitenant.check;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.json.JSONException;
import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.repository.AllSettings;
import org.smartregister.sync.helper.SyncSettingsServiceHelper;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 17-04-2020.
 */
public class SettingsSyncedCheck implements PreResetAppCheck{

    public static final String UNIQUE_NAME = "SettingsSyncedCheck";

    @WorkerThread
    @Override
    public boolean isCheckOk(@NonNull DrishtiApplication drishtiApplication) {
        return isSettingsSynced(drishtiApplication);
    }

    @WorkerThread
    @Override
    public void performPreResetAppOperations(@NonNull DrishtiApplication application) throws PreResetAppOperationException {
        SyncSettingsServiceHelper syncSettingsServiceHelper = new SyncSettingsServiceHelper(application.getContext().configuration().dristhiBaseURL(), application.getContext().getHttpAgent());
        try {
            syncSettingsServiceHelper.processIntent();
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    public boolean isSettingsSynced(@NonNull DrishtiApplication application) {
        AllSettings allSettings = application.getContext().allSettings();
        return allSettings.getUnsyncedSettingsCount() == 0;
    }

    @NonNull
    @Override
    public String getUniqueName() {
        return UNIQUE_NAME;
    }
}
