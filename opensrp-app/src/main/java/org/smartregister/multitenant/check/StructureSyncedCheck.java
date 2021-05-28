package org.smartregister.multitenant.check;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.smartregister.domain.FetchStatus;
import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.StructureRepository;
import org.smartregister.sync.helper.LocationServiceHelper;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 16-04-2020.
 */
public class StructureSyncedCheck implements PreResetAppCheck, SyncStatusBroadcastReceiver.SyncStatusListener {

    public static final String UNIQUE_NAME = "StructureSyncedCheck";

    @WorkerThread
    @Override
    public boolean isCheckOk(@NonNull DrishtiApplication drishtiApplication) {
        return isStructuresSynced(drishtiApplication);
    }

    @Override
    public void performPreResetAppOperations(@NonNull DrishtiApplication application) throws PreResetAppOperationException {
        org.smartregister.Context context = application.getContext();
        LocationServiceHelper locationServiceHelper = new LocationServiceHelper(
                context.getLocationRepository(),
                context.getLocationTagRepository(),
                context.getStructureRepository());
        locationServiceHelper.syncCreatedStructureToServer();
    }

    @WorkerThread
    public boolean isStructuresSynced(@NonNull DrishtiApplication application) {
        StructureRepository structureRepository = application.getContext().getStructureRepository();
        return structureRepository.getUnsyncedStructuresCount() == 0;
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
