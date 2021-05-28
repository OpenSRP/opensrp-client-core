package org.smartregister.sync.intent;

import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.sync.helper.SyncSettingsServiceHelper;

import timber.log.Timber;

import static org.smartregister.util.Log.logError;

/**
 * Created by ndegwamartin on 14/09/2018.
 */
public class SettingsSyncIntentService extends BaseSyncIntentService {
    public static final String SETTINGS_URL = "/rest/settings/sync";

    private static final String TAG = SettingsSyncIntentService.class.getCanonicalName();

    protected SyncSettingsServiceHelper syncSettingsServiceHelper;

    public static final String EVENT_SYNC_COMPLETE = "event_sync_complete";

    public SettingsSyncIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean isSuccessfulSync = processSettings(intent);
        if (isSuccessfulSync) {
            SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        }
    }

    protected boolean processSettings(Intent intent) {
        Timber.d("In Settings Sync Intent Service...");
        boolean isSuccessfulSync = true;
        if (intent != null) {
            try {
                super.onHandleIntent(intent);
                int count = syncSettingsServiceHelper.processIntent();
                if (count > 0) {
                    intent.putExtra(AllConstants.INTENT_KEY.SYNC_TOTAL_RECORDS, count);
                }
            } catch (JSONException e) {
                isSuccessfulSync = false;
                logError(TAG + " Error fetching client settings");
            }
        }
        return isSuccessfulSync;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = CoreLibrary.getInstance().context();
        syncSettingsServiceHelper = new SyncSettingsServiceHelper(context.configuration().dristhiBaseURL(), context.getHttpAgent());
    }

}

