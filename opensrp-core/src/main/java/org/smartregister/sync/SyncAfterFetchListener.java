package org.smartregister.sync;

import org.smartregister.domain.FetchStatus;

import static org.smartregister.event.Event.ON_DATA_FETCHED;

public class SyncAfterFetchListener implements AfterFetchListener {
    public void afterFetch(FetchStatus status) {
        ON_DATA_FETCHED.notifyListeners(status);
    }
}
