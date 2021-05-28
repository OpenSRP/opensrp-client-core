package org.smartregister.util;

import android.app.Service;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.util.mock.OpenSRPImageLoaderTestActivity;

import static org.mockito.Mockito.doReturn;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({Volley.class})
public class OpenSRPImageLoaderTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private OpenSRPImageLoaderTestActivity activity;

    @Mock
    private Context context;

    @Mock
    private CoreLibrary coreLibrary;

    private ActivityController<OpenSRPImageLoaderTestActivity> controller;

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        Intent intent = new Intent(RuntimeEnvironment.application, OpenSRPImageLoaderTestActivity.class);
        controller = Robolectric.buildActivity(OpenSRPImageLoaderTestActivity.class, intent);
        activity = controller.get();
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        doReturn(context).when(coreLibrary).context();
        controller.setup();
    }

    @Test
    public void assertActivityNotNull() {
        Assert.assertNotNull(activity);
    }

    @Test
    public void assertConstructorInitializationNotNull() throws Exception {
        OpenSRPImageLoader openSRPImageLoader = new OpenSRPImageLoader(activity.getInstance());
        Assert.assertNotNull(openSRPImageLoader);
    }

    @Test
    public void assertFragmentActivityConstructorInitializationNotNull() throws Exception {
        OpenSRPImageLoader openSRPImageLoader = new OpenSRPImageLoader(activity, -1);
        Assert.assertNotNull(openSRPImageLoader);
    }

    @Test
    public void assertServiceConstructorInitializationNotNull() throws Exception {
        PowerMockito.mockStatic(Volley.class);
        PowerMockito.when(Volley.newRequestQueue(Mockito.any(android.content.Context.class), Mockito.any(HurlStack.class))).thenReturn(Mockito.mock(RequestQueue.class));
        OpenSRPImageLoader openSRPImageLoader = new OpenSRPImageLoader(Mockito.mock(Service.class), -1);
        Assert.assertNotNull(openSRPImageLoader);
    }
}
