package org.smartregister.broadcastreceivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.AllConstants;
import org.smartregister.R;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.activity.SecuredActivity;

import timber.log.Timber;

/**
 * Created by onamacuser on 16/03/2016.
 */
public class OpenSRPClientBroadCastReceiver extends BroadcastReceiver {
    Activity activity;

    public OpenSRPClientBroadCastReceiver(Activity _activity) {
        activity = _activity;

    }

    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            switch (action) {

                case Intent.ACTION_TIME_CHANGED:
                    //((SecuredActivity) activity).showToast("TIME CHANGED");
                    Timber.d("timechanged");
                    forceFullySignOut();
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    //((SecuredActivity) activity).showToast("TIMEZONE CHANGED");
                    Timber.d("timezonechanged");
                    forceFullySignOut();
                    break;
                case AllConstants.CloudantSync.ACTION_DATABASE_CREATED:
//                      ((SecuredActivity) activity).loadDatabase();
                    break;
                case AllConstants.CloudantSync.ACTION_REPLICATION_COMPLETED:
//                    Integer docsReplicated = intent.getIntExtra(
//                            AllConstants.CloudantSync.DOCUMENTS_REPLICATED, 0);
//                    Integer batchReplicated = intent.getIntExtra(
//                            AllConstants.CloudantSync.BATCHES_REPLICATED, 0);
//                    ((SecuredActivity) activity).showToast("Replication completed.");
                    break;
                case AllConstants.CloudantSync.ACTION_REPLICATION_ERROR:
                    ((SecuredActivity) activity).showToast(context.getString(R.string.replication_error_occurred));
                    break;
                default:
                    // Do nothing
            }
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    private void forceFullySignOut() {
        DrishtiApplication application = (DrishtiApplication) activity.getApplication();
        application.logoutCurrentUser();
    }
}
