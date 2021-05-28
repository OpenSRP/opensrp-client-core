package org.smartregister.sync.intent;

import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.service.ActionService;
import org.smartregister.service.DrishtiService;
import org.smartregister.shadows.BaseJobShadow;
import org.smartregister.shadows.ShadowNetworkUtils;

/**
 * Created by Vincent Karuri on 19/01/2021
 */
public class ExtendedSyncIntentServiceTest extends BaseUnitTest {

    private final DrishtiService drishtiService = new DrishtiService(CoreLibrary.getInstance().context().httpAgent(), "http://localhost");

    private ExtendedSyncIntentService extendedSyncIntentService;

    @Before
    public void setUp() {
        extendedSyncIntentService = new ExtendedSyncIntentService();
    }

    @Config(shadows = {ShadowNetworkUtils.class, BaseJobShadow.class})
    @Test
    public void testOnHandleIntentShouldStartSyncValidation() {
        ShadowNetworkUtils.setIsNetworkAvailable(true);

        ActionService actionService = ReflectionHelpers.getField(extendedSyncIntentService, "actionService");
        actionService = Mockito.spy(actionService);
        ReflectionHelpers.setField(extendedSyncIntentService, "actionService", actionService);
        ReflectionHelpers.setField(actionService, "drishtiService", drishtiService);
        Assert.assertEquals(0, BaseJobShadow.getMockCounter().getCount());

        extendedSyncIntentService.onHandleIntent(new Intent());

        Mockito.verify(actionService).fetchNewActions();
        ShadowNetworkUtils.setIsNetworkAvailable(false);
        Assert.assertEquals(1, BaseJobShadow.getMockCounter().getCount());

        BaseJobShadow.getMockCounter().setCount(0);
    }
}