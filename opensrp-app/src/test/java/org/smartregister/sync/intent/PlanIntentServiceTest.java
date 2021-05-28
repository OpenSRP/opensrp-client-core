package org.smartregister.sync.intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.sync.helper.PlanIntentServiceHelper;

/**
 * Created by Vincent Karuri on 18/05/2021
 */
public class PlanIntentServiceTest extends BaseUnitTest {

    private PlanIntentService planIntentService;

    @Before
    public void setUp() {
        planIntentService = new PlanIntentService();
    }

    @Test
    public void testOnHandleIntentShouldSyncPlans() throws Exception {
        PlanIntentServiceHelper planIntentServiceHelper = Mockito.mock(PlanIntentServiceHelper.class);
        Whitebox.setInternalState(PlanIntentServiceHelper.class, "instance", planIntentServiceHelper);
        Whitebox.invokeMethod(planIntentService, "onHandleIntent", null);
        Mockito.verify(planIntentServiceHelper).syncPlans();
        Whitebox.setInternalState(PlanIntentServiceHelper.class, "instance", (PlanIntentServiceHelper) null);
    }
}