package org.smartregister.sync.intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.sync.helper.LocationServiceHelper;

/**
 * Created by Vincent Karuri on 18/05/2021
 */
public class SyncLocationsByLevelAndTagsIntentServiceTest extends BaseUnitTest {

    private SyncLocationsByLevelAndTagsIntentService syncLocationsByLevelAndTagsIntentService;

    @Before
    public void setUp() throws Exception {
        syncLocationsByLevelAndTagsIntentService = new SyncLocationsByLevelAndTagsIntentService();
    }

    @Test
    public void testOnHandleIntentShouldFetchLocationsByLevelAndTags() throws Exception {
        LocationServiceHelper locationServiceHelper = Mockito.mock(LocationServiceHelper.class);
        Whitebox.setInternalState(LocationServiceHelper.class, "instance", locationServiceHelper);
        Whitebox.invokeMethod(syncLocationsByLevelAndTagsIntentService, "onHandleIntent", null);
        Mockito.verify(locationServiceHelper).fetchLocationsByLevelAndTags();
        Whitebox.setInternalState(LocationServiceHelper.class, "instance", (LocationServiceHelper) null);
    }
}