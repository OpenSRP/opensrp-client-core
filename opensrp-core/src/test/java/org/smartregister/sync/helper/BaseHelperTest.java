package org.smartregister.sync.helper;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 10-11-2020.
 */
public class BaseHelperTest extends BaseRobolectricUnitTest {

    @Test
    public void getFormattedBaseUrl() {
        String url = "http://site.com/";

        DristhiConfiguration dristhiConfiguration = Mockito.spy(CoreLibrary.getInstance().context().configuration());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "configuration", dristhiConfiguration);
        Mockito.doReturn(url).when(dristhiConfiguration).dristhiBaseURL();

        Assert.assertNotNull(CoreLibrary.getInstance().context().configuration().dristhiBaseURL());

        Assert.assertEquals("http://site.com", new BaseHelper().getFormattedBaseUrl());

        // Return the configuration to the previous state to fix the TaskServiceHelperTest failures
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }
}