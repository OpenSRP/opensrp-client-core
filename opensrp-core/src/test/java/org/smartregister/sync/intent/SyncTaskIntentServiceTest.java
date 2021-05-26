package org.smartregister.sync.intent;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.sync.helper.TaskServiceHelper;

/**
 * Created by Vincent Karuri on 30/03/2021
 */
public class SyncTaskIntentServiceTest extends BaseUnitTest {

    @Test
    public void testOnHandleIntentShouldSyncTasks() {
        SyncTaskIntentService syncTaskIntentService = new SyncTaskIntentService();
        TaskServiceHelper taskServiceHelper = Mockito.mock(TaskServiceHelper.class);
        Whitebox.setInternalState(syncTaskIntentService, "taskServiceHelper", taskServiceHelper);
        syncTaskIntentService.onHandleIntent(null);
        Mockito.verify(taskServiceHelper).syncTasks();
    }
}