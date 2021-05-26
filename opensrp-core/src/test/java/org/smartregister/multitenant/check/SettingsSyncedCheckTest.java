package org.smartregister.multitenant.check;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.DristhiConfiguration;
import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.repository.AllSettings;
import org.smartregister.shadows.ShadowSyncSettingsServiceHelper;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 11-08-2020.
 */
public class SettingsSyncedCheckTest extends BaseRobolectricUnitTest {

    public SettingsSyncedCheck settingsSyncedCheck;

    @Before
    public void setUp() throws Exception {
        settingsSyncedCheck = Mockito.spy(new SettingsSyncedCheck());
    }

    @Test
    public void isCheckOk() {
        Mockito.doReturn(true).when(settingsSyncedCheck).isSettingsSynced(DrishtiApplication.getInstance());

        settingsSyncedCheck.isCheckOk(DrishtiApplication.getInstance());

        Mockito.verify(settingsSyncedCheck).isSettingsSynced(DrishtiApplication.getInstance());
    }

    @Config(shadows = {ShadowSyncSettingsServiceHelper.class})
    @Test
    public void performPreResetAppOperations() throws PreResetAppOperationException, JSONException {
        DristhiConfiguration dristhiConfiguration = Mockito.spy(DrishtiApplication.getInstance().getContext().configuration());
        Mockito.doReturn("https://someurl.com").when(dristhiConfiguration).dristhiBaseURL();
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "configuration", dristhiConfiguration);

        settingsSyncedCheck.performPreResetAppOperations(DrishtiApplication.getInstance());

        Assert.assertEquals(1, ShadowSyncSettingsServiceHelper.processIntent);
    }

    @Test
    public void isSettingsSynced() {
        AllSettings allSettings = Mockito.spy(DrishtiApplication.getInstance().getContext().allSettings());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "allSettings", allSettings);

        Assert.assertTrue(settingsSyncedCheck.isSettingsSynced(DrishtiApplication.getInstance()));

        Mockito.verify(allSettings).getUnsyncedSettingsCount();
    }
}