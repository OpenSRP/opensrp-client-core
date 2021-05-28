package org.smartregister.multitenant.check;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import org.smartregister.sync.intent.SyncIntentService;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class EventClientSync extends SyncIntentService {

    private Context context;

    public EventClientSync(@NonNull Context context) {
        this.context = context;
        attachBaseContext(context);
    }

    protected void performSync() {
        init(context);
        handleSync();
    }

    @Override
    protected void pullECFromServer() {
        // Do not pull from EC Server
    }
}
