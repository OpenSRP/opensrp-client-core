package org.smartregister.sync.intent;

import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.service.DocumentConfigurationService;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 25-08-2020.
 */
public class DocumentConfigurationIntentServiceTest extends BaseRobolectricUnitTest {

    private DocumentConfigurationIntentService documentConfigurationIntentService;

    @Before
    public void setUp() throws Exception {
        documentConfigurationIntentService = Mockito.spy(Robolectric.buildIntentService(DocumentConfigurationIntentService.class)
                .create()
                .get());
    }

    @After
    public void tearDown() throws Exception {
        initCoreLibrary();
    }

    @Test
    public void onStartCommandShouldInstantiateVariables() {
        Assert.assertNull(ReflectionHelpers.getField(documentConfigurationIntentService, "httpAgent"));
        Assert.assertNull(ReflectionHelpers.getField(documentConfigurationIntentService, "manifestRepository"));
        Assert.assertNull(ReflectionHelpers.getField(documentConfigurationIntentService, "clientFormRepository"));
        Assert.assertNull(ReflectionHelpers.getField(documentConfigurationIntentService, "configuration"));

        documentConfigurationIntentService.onStartCommand(null, 0, 900);


        Assert.assertNotNull(ReflectionHelpers.getField(documentConfigurationIntentService, "httpAgent"));
        Assert.assertNotNull(ReflectionHelpers.getField(documentConfigurationIntentService, "manifestRepository"));
        Assert.assertNotNull(ReflectionHelpers.getField(documentConfigurationIntentService, "clientFormRepository"));
        Assert.assertNotNull(ReflectionHelpers.getField(documentConfigurationIntentService, "configuration"));
    }

    @Test
    public void onHandleIntentShouldCallDocumentConfigurationServiceFetchManifest() throws JSONException, NoHttpResponseException {
        DocumentConfigurationService documentConfigurationService = Mockito.mock(DocumentConfigurationService.class);
        Mockito.doReturn(documentConfigurationService).when(documentConfigurationIntentService).getDocumentConfigurationService();

        documentConfigurationIntentService.onHandleIntent(null);

        Mockito.verify(documentConfigurationService).fetchManifest();
        Mockito.verify(documentConfigurationIntentService).getDocumentConfigurationService();
    }
}