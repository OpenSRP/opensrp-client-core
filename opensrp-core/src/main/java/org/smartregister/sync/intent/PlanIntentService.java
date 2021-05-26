package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;

import org.smartregister.sync.helper.PlanIntentServiceHelper;

/**
 * Created by Vincent Karuri on 08/05/2019
 */
public class PlanIntentService extends BaseSyncIntentService {

    private static final String TAG = "PlanIntentService";

    public PlanIntentService() { super(TAG); }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        PlanIntentServiceHelper.getInstance().syncPlans();
    }
}
