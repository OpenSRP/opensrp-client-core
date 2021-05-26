package org.smartregister.multitenant.check;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.smartregister.domain.FetchStatus;
import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class EventClientSyncedCheck implements PreResetAppCheck, SyncStatusBroadcastReceiver.SyncStatusListener {

    public static final String UNIQUE_NAME = "EventClientSyncedCheck";

    @WorkerThread
    @Override
    public boolean isCheckOk(@NonNull DrishtiApplication drishtiApplication) {
        return isEventsClientSynced(drishtiApplication);
    }

    @WorkerThread
    @Override
    public void performPreResetAppOperations(@NonNull DrishtiApplication application) throws PreResetAppOperationException {
        SyncStatusBroadcastReceiver.init(application.getBaseContext());
        SyncStatusBroadcastReceiver syncStatusBroadcastReceiver = SyncStatusBroadcastReceiver.getInstance();
        syncStatusBroadcastReceiver.addSyncStatusListener(this);

        EventClientSync syncIntentService = new EventClientSync(application);
        syncIntentService.performSync();

        syncStatusBroadcastReceiver.removeSyncStatusListener(this);
    }

    public boolean isEventsClientSynced(@NonNull DrishtiApplication application) {
        EventClientRepository eventClientRepository = application.getContext().getEventClientRepository();
        if (eventClientRepository != null) {
            return eventClientRepository.getUnSyncedEventsCount() == 0;
        }

        return false;
    }

    @Override
    public void onSyncStart() {
        // Do nothing for now
        Timber.e("Sync is starting");
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        if (fetchStatus == FetchStatus.fetchProgress) {
            Timber.e("Sync progress is %s", fetchStatus.displayValue());
        }
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        // Do nothing for now
        Timber.e("The sync is complete");
    }

    @NonNull
    @Override
    public String getUniqueName() {
        return UNIQUE_NAME;
    }
}
