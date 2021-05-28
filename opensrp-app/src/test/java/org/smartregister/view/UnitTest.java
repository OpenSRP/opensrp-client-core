package org.smartregister.view;

import org.junit.After;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.TestApplication;

/**
 * Created by Vincent Karuri on 02/02/2021
 */

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class)
public abstract class UnitTest {

    @After
    public void tearDown() throws Exception {
        Context.destroyInstance();
        CoreLibrary.destroyInstance();
    }
}
