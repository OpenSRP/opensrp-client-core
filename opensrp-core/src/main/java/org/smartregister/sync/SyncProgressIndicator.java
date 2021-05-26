package org.smartregister.sync;

import org.smartregister.CoreLibrary;
import org.smartregister.view.ProgressIndicator;

import static org.smartregister.event.Event.SYNC_COMPLETED;
import static org.smartregister.event.Event.SYNC_STARTED;

public class SyncProgressIndicator implements ProgressIndicator {
    @Override
    public void setVisible() {
        CoreLibrary.getInstance().context().allSharedPreferences().saveIsSyncInProgress(true);
        SYNC_STARTED.notifyListeners(true);
    }

    @Override
    public void setInvisible() {
        CoreLibrary.getInstance().context().allSharedPreferences().saveIsSyncInProgress(false);
        SYNC_COMPLETED.notifyListeners(true);
    }
}
