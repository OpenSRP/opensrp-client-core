package org.smartregister.multitenant.check;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public interface PreResetAppCheck {

    @WorkerThread
    boolean isCheckOk(@NonNull DrishtiApplication drishtiApplication);

    @WorkerThread
    void performPreResetAppOperations(@NonNull DrishtiApplication drishtiApplication) throws PreResetAppOperationException;

    @NonNull
    String getUniqueName();
}
