package org.smartregister.util;

import android.content.Context;
import android.content.pm.PackageManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.domain.Setting;
import org.smartregister.repository.AllSettings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.smartregister.AllConstants.FORCED_LOGOUT.MIN_ALLOWED_APP_VERSION;
import static org.smartregister.AllConstants.FORCED_LOGOUT.MIN_ALLOWED_APP_VERSION_SETTING;

/**
 * Created by Vincent Karuri on 10/03/2020
 */

@PrepareForTest(Utils.class)
public class SyncUtilsTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Mock
    private org.smartregister.Context opensrpContext;

    @Mock
    private AllSettings settingsRepository;

    private SyncUtils syncUtils;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private CoreLibrary coreLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        doReturn(opensrpContext).when(coreLibrary).context();
        syncUtils = new SyncUtils(context);
        doReturn(settingsRepository).when(opensrpContext).allSettings();
    }

    @Test
    public void testIsAppVersionAllowedShouldReturnCorrectStatus() throws PackageManager.NameNotFoundException {
        mockStatic(Utils.class);

        // setting doesn't exist
        assertTrue(syncUtils.isAppVersionAllowed());

        // outdated app
        Setting setting = new Setting();
        setting.setIdentifier(MIN_ALLOWED_APP_VERSION_SETTING);
        setting.setValue(getMinAppVersionSetting(2));
        doReturn(setting).when(settingsRepository).getSetting(eq(MIN_ALLOWED_APP_VERSION_SETTING));
        when(Utils.getVersionCode(any(Context.class))).thenReturn(1l);
        assertFalse(syncUtils.isAppVersionAllowed());

        // same version app
        when(Utils.getVersionCode(any())).thenReturn(2l);
        doReturn(setting).when(settingsRepository).getSetting(eq(MIN_ALLOWED_APP_VERSION_SETTING));
        assertTrue(syncUtils.isAppVersionAllowed());

        // newer version app
        when(Utils.getVersionCode(any())).thenReturn(3l);
        doReturn(setting).when(settingsRepository).getSetting(eq(MIN_ALLOWED_APP_VERSION_SETTING));
        assertTrue(syncUtils.isAppVersionAllowed());

        // 1. outdated app
        doReturn("2").when(settingsRepository).get(eq(MIN_ALLOWED_APP_VERSION));
        when(Utils.getVersionCode(any())).thenReturn(1l);
        assertFalse(syncUtils.isAppVersionAllowed());

        // 2. same version app
        when(Utils.getVersionCode(any())).thenReturn(2l);
        assertTrue(syncUtils.isAppVersionAllowed());

        // 3. newer version app
        when(Utils.getVersionCode(any())).thenReturn(3l);
        assertTrue(syncUtils.isAppVersionAllowed());
    }

    @Test
    public void testGetNumOfSyncAttemptsShouldGetCorrectNum() {
        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);
        Mockito.doReturn(syncConfiguration).when(CoreLibrary.getInstance()).getSyncConfiguration();

        Mockito.doReturn(3).when(syncConfiguration).getSyncMaxRetries();
        Assert.assertEquals(4, syncUtils.getNumOfSyncAttempts());

        Mockito.doReturn(0).when(syncConfiguration).getSyncMaxRetries();
        Assert.assertEquals(1, syncUtils.getNumOfSyncAttempts());

        Mockito.doReturn(-10).when(syncConfiguration).getSyncMaxRetries();
        Assert.assertEquals(1, syncUtils.getNumOfSyncAttempts());
    }

    private String getMinAppVersionSetting(int minVersion) {
        String setting = "{\n" +
                "  \"identifier\": \"min_allowed_app_version\",\n" +
                "  \"settings\": [\n" +
                "    {\n" +
                "      \"description\": \"Defines the minimum allowed version of the client app allowed to sync to this server\",\n" +
                "      \"label\": \"Minimum allowed application version\",\n" +
                "      \"value\": \"" + minVersion + "\",\n" +
                "      \"key\": \"min_allowed_app_version_setting\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"serverVersion\": 1583417991264,\n" +
                "  \"_rev\": \"v2\",\n" +
                "  \"_id\": \"81dca35c-a88a-4a32-bd0e-11ee716a0369\",\n" +
                "  \"type\": \"SettingConfiguration\"\n" +
                "}";

        return setting;
    }
}
