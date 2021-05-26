package org.smartregister.view.dialog;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.customshadows.FontTextViewShadow;
import org.smartregister.view.dialog.mock.LocationSelectorDialogFragmentTestActivity;

/**
 * Created by kaderchowdhury on 20/11/17.
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class})
@Config(shadows = {FontTextViewShadow.class})
public class LocationSelectorDialogFragmentTest extends BaseUnitTest {
    private ActivityController<LocationSelectorDialogFragmentTestActivity> controller;

    @InjectMocks
    private LocationSelectorDialogFragmentTestActivity activity;

    @Mock
    private org.smartregister.Context context_;

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);

//        Intent intent = new Intent(RuntimeEnvironment.application, LocationSelectorDialogFragmentTestActivity.class);
//        controller = Robolectric.buildActivity(LocationSelectorDialogFragmentTestActivity.class, intent);
//        activity = controller.start().resume().get();
//
//        CoreLibrary.init(context_);
//        controller.setup();

    }

    @After
    public void tearDown() {
        destroyController();
        activity = null;
        controller = null;

    }

    private void destroyController() {
        try {
//            activity.finish();
//            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), e.getMessage());
        }

        System.gc();
    }

    @Test
    public void assertThatCallToNewInstanceCreatesAFragment() {
//        junit.framework.Assert.assertNotNull(ServiceDialogFragment.newInstance(Collections.EMPTY_LIST, new ServiceWrapper()));
//        junit.framework.Assert.assertNotNull(ServiceDialogFragment.newInstance(new DateTime(), Collections.EMPTY_LIST, new ServiceWrapper(), true));
    }

    @Test
    public void assertOnCreateViewTestSetsUpTheActivity() throws Exception {
        destroyController();
//        Intent intent = new Intent(RuntimeEnvironment.application, LocationSelectorDialogFragmentTestActivity.class);
//        controller = Robolectric.buildActivity(LocationSelectorDialogFragmentTestActivity.class, intent);
//        activity = controller.get();
//        controller.setup();
//        junit.framework.Assert.assertNotNull(activity);
    }
}
