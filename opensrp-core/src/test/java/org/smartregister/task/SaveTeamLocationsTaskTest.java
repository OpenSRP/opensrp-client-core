package org.smartregister.task;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.location.helper.LocationHelper;

/**
 * Created by Vincent Karuri on 30/03/2021
 */
public class SaveTeamLocationsTaskTest extends BaseUnitTest {

    @Test
    public void testDoInBackgroundShouldSaveTeamLocations() {
        LocationHelper locationHelper = Mockito.mock(LocationHelper.class);
        Whitebox.setInternalState(LocationHelper.class, "instance", locationHelper);
        new SaveTeamLocationsTask().execute();
        Mockito.verify(locationHelper).locationIdsFromHierarchy();
        Whitebox.setInternalState(LocationHelper.class, "instance", (LocationHelper) null);
    }
}