package org.smartregister.sync.intent;

import android.content.Intent;

import org.smartregister.sync.helper.LocationServiceHelper;

import timber.log.Timber;

public class SyncLocationsByLevelAndTagsIntentService extends BaseSyncIntentService {

    private static final String TAG = "SyncLocationsByLevelAndTagsIntentService";

    public SyncLocationsByLevelAndTagsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        LocationServiceHelper locationServiceHelper = LocationServiceHelper.getInstance();

        try {
            locationServiceHelper.fetchLocationsByLevelAndTags();
        } catch (Exception e){
            Timber.e(e);
        }
    }
}