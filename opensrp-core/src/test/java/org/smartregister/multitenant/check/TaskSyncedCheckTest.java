package org.smartregister.multitenant.check;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.helper.TaskServiceHelper;
import org.smartregister.view.activity.DrishtiApplication;

import static org.junit.Assert.assertTrue;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 22-04-2020.
 */
public class TaskSyncedCheckTest extends BaseRobolectricUnitTest {

    private TaskSyncedCheck taskSyncedCheck;

    @Before
    public void setUp() throws Exception {
        taskSyncedCheck = Mockito.spy(new TaskSyncedCheck());
    }

    @After
    public void tearDown() throws Exception {
        // This fixes an issue where TaskServiceHelperTest fails due to the TaskServiceHelper being initialised and state
        // changed from this tests onwards
        ReflectionHelpers.setStaticField(TaskServiceHelper.class, "instance", null);
    }

    @Test
    public void isCheckOkShouldCallIsTaskSynced() {
        Mockito.doReturn(false).when(taskSyncedCheck).isTaskSynced(Mockito.eq(DrishtiApplication.getInstance()));
        taskSyncedCheck.isCheckOk(DrishtiApplication.getInstance());

        Mockito.verify(taskSyncedCheck).isTaskSynced(Mockito.eq(DrishtiApplication.getInstance()));
    }

    @Test
    public void performPreResetAppOperations() throws PreResetAppOperationException {
        TaskServiceHelper taskServiceHelper = Mockito.mock(TaskServiceHelper.class);

        ReflectionHelpers.setStaticField(TaskServiceHelper.class, "instance", taskServiceHelper);

        taskSyncedCheck.performPreResetAppOperations(DrishtiApplication.getInstance());

        Mockito.verify(taskServiceHelper).syncCreatedTaskToServer();
        Mockito.verify(taskServiceHelper).syncTaskStatusToServer();
    }

    @Test
    public void isTaskSynced() {
        TaskRepository taskRepository = Mockito.spy(DrishtiApplication.getInstance().getContext().getTaskRepository());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "taskRepository", taskRepository);

        Mockito.doReturn(0).when(taskRepository).getUnsyncedCreatedTasksAndTaskStatusCount();

        assertTrue(taskSyncedCheck.isTaskSynced(DrishtiApplication.getInstance()));
        Mockito.verify(taskRepository).getUnsyncedCreatedTasksAndTaskStatusCount();
    }
}