package org.smartregister.sync.intent;

import android.content.Intent;

import org.smartregister.CoreLibrary;
import org.smartregister.job.ValidateSyncDataServiceJob;
import org.smartregister.service.ActionService;
import org.smartregister.util.NetworkUtils;

import timber.log.Timber;


public class ExtendedSyncIntentService extends BaseSyncIntentService {

    private ActionService actionService = CoreLibrary.getInstance().context().actionService();

    public ExtendedSyncIntentService() {
        super("ExtendedSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        try {
            super.onHandleIntent(workIntent);
            if (NetworkUtils.isNetworkAvailable()) {
                if (!CoreLibrary.getInstance().getSyncConfiguration().disableActionService()) {
                    actionService.fetchNewActions();
                }
                startSyncValidation();
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void startSyncValidation() {
        ValidateSyncDataServiceJob.scheduleJobImmediately(ValidateSyncDataServiceJob.TAG);
    }
}
