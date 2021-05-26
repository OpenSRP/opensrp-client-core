package org.smartregister.service.intentservices;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by onamacuser on 18/03/2016.
 */
public class ReplicationIntentService extends IntentService {
    private static final String TAG = ReplicationIntentService.class.getCanonicalName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ReplicationIntentService(String name) {
        super(name);
    }

    public ReplicationIntentService() {

        super("ReplicationIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }
}
