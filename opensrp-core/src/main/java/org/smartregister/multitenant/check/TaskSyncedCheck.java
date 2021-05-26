package org.smartregister.multitenant.check;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.helper.TaskServiceHelper;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 16-04-2020.
 */
public class TaskSyncedCheck implements PreResetAppCheck{

    public static final String UNIQUE_NAME = "TaskSyncedCheck";

    @WorkerThread
    @Override
    public boolean isCheckOk(@NonNull DrishtiApplication drishtiApplication) {
        return isTaskSynced(drishtiApplication);
    }

    @WorkerThread
    @Override
    public void performPreResetAppOperations(@NonNull DrishtiApplication application) throws PreResetAppOperationException {
        TaskServiceHelper taskServiceHelper = TaskServiceHelper.getInstance();
        taskServiceHelper.syncCreatedTaskToServer();
        taskServiceHelper.syncTaskStatusToServer();
    }

    public boolean isTaskSynced(@NonNull DrishtiApplication application) {
        TaskRepository taskRepository = application.getContext().getTaskRepository();
        return taskRepository.getUnsyncedCreatedTasksAndTaskStatusCount() == 0;
    }

    @NonNull
    @Override
    public String getUniqueName() {
        return UNIQUE_NAME;
    }
}
