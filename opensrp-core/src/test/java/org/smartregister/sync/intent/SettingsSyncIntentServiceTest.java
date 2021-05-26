package org.smartregister.sync.intent;

import android.content.Intent;

import com.evernote.android.job.JobRequest;
import com.evernote.android.job.ShadowJobManager;

import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 25-08-2020.
 */
public class SettingsSyncIntentServiceTest extends BaseRobolectricUnitTest {

    private SettingsSyncIntentService settingsSyncIntentService;

    @Before
    public void setUp() throws Exception {
        // Clear the following
        ShadowJobManager.jobStorage = null;
        ShadowJobManager.mockJobManager = null;
        ShadowJobManager.createMockJobManager();

        settingsSyncIntentService = Robolectric.buildIntentService(SettingsSyncIntentService.class)
                .create()
                .get();
    }

    @After
    public void tearDown() throws Exception {
        initCoreLibrary();
    }

    @Test
    public void onHandleIntentShouldScheduleJobRequestAndInvokeProcessSettingsWhenGivenNullIntent() {
        settingsSyncIntentService = Mockito.spy(settingsSyncIntentService);
        settingsSyncIntentService.onHandleIntent(null);

        Mockito.verify(ShadowJobManager.mockJobManager).schedule(Mockito.any(JobRequest.class));
        Mockito.verify(settingsSyncIntentService).processSettings(Mockito.nullable(Intent.class));
    }

    @Test
    public void processSettingsShouldReturnTrueWhenIntentIsNull() throws JSONException {
        settingsSyncIntentService.syncSettingsServiceHelper = Mockito.spy(settingsSyncIntentService.syncSettingsServiceHelper);
        Assert.assertTrue(settingsSyncIntentService.processSettings(null));

        Mockito.verify(settingsSyncIntentService.syncSettingsServiceHelper, Mockito.never()).processIntent();
    }

    @Test
    public void processSettingsShouldReturnTrueAndCallProcessIntentWhenIntentIsNotNull() throws JSONException {
        settingsSyncIntentService.syncSettingsServiceHelper = Mockito.spy(settingsSyncIntentService.syncSettingsServiceHelper);
        Mockito.doReturn(30).when(settingsSyncIntentService.syncSettingsServiceHelper).processIntent();

        Intent intent = new Intent();

        Assert.assertTrue(settingsSyncIntentService.processSettings(intent));
        Mockito.verify(settingsSyncIntentService.syncSettingsServiceHelper, Mockito.times(1)).processIntent();

        Assert.assertEquals(30, intent.getIntExtra(AllConstants.INTENT_KEY.SYNC_TOTAL_RECORDS, 0));
    }

    @Test
    public void onCreateShouldCreateSyncSettingsServiceHelper() {
        settingsSyncIntentService = Robolectric.buildIntentService(SettingsSyncIntentService.class)
                .get();
        Assert.assertNull(settingsSyncIntentService.syncSettingsServiceHelper);

        settingsSyncIntentService.onCreate();

        Assert.assertNotNull(settingsSyncIntentService.syncSettingsServiceHelper);
    }

}