package org.smartregister.sync.intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.annotation.Config;
import org.smartregister.BaseUnitTest;
import org.smartregister.shadows.ShadowLocationServiceHelper;
import org.smartregister.sync.helper.LocationServiceHelper;

/**
 * Created by Vincent Karuri on 02/03/2021
 */

@Config(shadows = {ShadowLocationServiceHelper.class})
public class LocationIntentServiceTest extends BaseUnitTest {

    @Mock
    private LocationServiceHelper locationServiceHelper;

    private LocationIntentService locationIntentService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        locationIntentService = new LocationIntentService();
    }

    @Test
    public void testOnHandleIntentShouldFetchLocations() throws Exception {
        ShadowLocationServiceHelper.setInstance(locationServiceHelper);
        Whitebox.invokeMethod(locationIntentService, "onHandleIntent", null);
        Mockito.verify(locationServiceHelper).fetchLocationsStructures();
    }
}