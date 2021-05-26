package org.smartregister.view.viewholder;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.view.activity.SecuredActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 2020-04-14.
 */
public class OnClickFormLauncherTest extends BaseUnitTest {

    @Mock
    private SecuredActivity activity;

    @Mock
    private View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnClickInvokesStartFormActivityWithCorrectParams() {

        String testMetadata = "{\"fieldOverrides\": \"metadata-override-values\"}";

        Mockito.doNothing().when(activity).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata);

        OnClickFormLauncher onClickFormLauncher = new OnClickFormLauncher(activity, TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata);
        Assert.assertNotNull(onClickFormLauncher);

        onClickFormLauncher.onClick(view);

        Mockito.verify(activity).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata);

    }

    @Test
    public void testOnClickInvokesStartFormActivityWithCorrectParamsNoMetadata() {

        Mockito.doNothing().when(activity).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, "");

        OnClickFormLauncher onClickFormLauncher = new OnClickFormLauncher(activity, TEST_FORM_NAME, TEST_BASE_ENTITY_ID);
        Assert.assertNotNull(onClickFormLauncher);

        onClickFormLauncher.onClick(view);

        Mockito.verify(activity).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, (String) null);

    }

    @Test
    public void testOnClickInvokesStartFormActivityWithCorrectMapMetadataParams() {
        Map<String, String> testMetadata = new HashMap<>();
        testMetadata.put("first_name", "john");
        testMetadata.put("last_name", "doe");

        Mockito.doNothing().when(activity).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata);

        OnClickFormLauncher onClickFormLauncher = new OnClickFormLauncher(activity, TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata.toString());
        Assert.assertNotNull(onClickFormLauncher);

        onClickFormLauncher.onClick(view);

        Mockito.verify(activity, Mockito.times(1)).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata.toString());
    }

    @Test
    public void testOnClickInvokesStartFormActivityWithEmptyMapMetadataParams() {
        Map<String, String> testMetadata = new HashMap<>();

        Mockito.doNothing().when(activity).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata);

        OnClickFormLauncher onClickFormLauncher = new OnClickFormLauncher(activity, TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata.toString());
        Assert.assertNotNull(onClickFormLauncher);

        onClickFormLauncher.onClick(view);

        Mockito.verify(activity, Mockito.times(1)).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata.toString());
    }

}
