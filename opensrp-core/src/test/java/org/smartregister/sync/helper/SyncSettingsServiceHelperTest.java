package org.smartregister.sync.helper;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.domain.Setting;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.service.HTTPAgent;

import java.util.ArrayList;
import java.util.List;

public class SyncSettingsServiceHelperTest extends BaseRobolectricUnitTest {
    private SyncSettingsServiceHelper syncSettingsServiceHelper;
    private CoreLibrary coreLibrary = Mockito.spy(CoreLibrary.getInstance());
    private Repository repository;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private HTTPAgent httpAgent;
    @Mock
    private Context context;
    @Mock
    private DristhiConfiguration dristhiConfiguration;
    @Mock
    private SyncConfiguration syncConfiguration;
    @Mock
    private AllSettings settings;
    @Mock
    private SQLiteDatabase sqLiteDatabase;
    private String settingsResponse = "[{\"identifier\":\"site_characteristics\",\"settings\":[{\"settingMetadataId\":\"4\",\"serverVersion\":1594125118616,\"description\":\"Is the HIV prevalence consistently &amp;gt; 1% in pregnant women attending antenatal clinics at your facility?\",\"label\":\"Generalized HIV epidemic\",\"type\":\"Setting\",\"value\":\"true\",\"uuid\":\"e42f3e1f-e8b9-4694-8efa-f021e66b5691\",\"key\":\"site_anc_hiv\",\"settingIdentifier\":\"site_characteristics\"},{\"settingMetadataId\":\"1\",\"serverVersion\":1594125118616,\"description\":\"\\\"Are all of the following in place at your facility: \\r\\n1. A protocol or standard operating procedure for Intimate Partner Violence (IPV); \\r\\n2. A health worker trained on how to ask about IPV and how to provide the minimum response or beyond;\\r\\n3. A private setting; \\r\\n4. A way to ensure confidentiality; \\r\\n5. Time to allow for appropriate disclosure; and\\r\\n6. A system for referral in place. \\\"\",\"label\":\"Minimum requirements for IPV assessment\",\"type\":\"Setting\",\"value\":\"true\",\"uuid\":\"fb2ca30f-3de5-4bfc-a2d2-987e9c383cd7\",\"key\":\"site_ipv_assess\",\"settingIdentifier\":\"site_characteristics\"}],\"serverVersion\":1593791975015,\"providerId\":\"demo\",\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"teamId\":\"6c8d2b9b-2246-47c2-949b-4fe29e888cc8\",\"_rev\":\"v1\",\"team\":\"Bukesa\",\"_id\":\"9918b87c-a71f-462d-b1c9-33a2d50e4c15\",\"type\":\"Setting\"}]";
    private static final String SAMPLE_TEST_TOKEN = "Sample_TOKEN";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn(context).when(coreLibrary).context();
        Mockito.doReturn(httpAgent).when(context).getHttpAgent();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();
        Mockito.doReturn(dristhiConfiguration).when(context).configuration();
        Mockito.doReturn("https://sample-stage.smartregister.org/opensrp/").when(dristhiConfiguration).dristhiBaseURL();
        syncSettingsServiceHelper = Mockito.spy(new SyncSettingsServiceHelper(context.configuration().dristhiBaseURL(), httpAgent));
        Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();
        repository = Mockito.mock(Repository.class, Mockito.CALLS_REAL_METHODS);
        Mockito.doReturn(sqLiteDatabase).when(repository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
    }

    @Test
    public void testProcessIntent() throws JSONException {
        AllSettings allSettings = Mockito.spy(CoreLibrary.getInstance().context().allSettings());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSettings", allSettings);
        SettingsRepository settingsRepository = ReflectionHelpers.getField(allSettings, "settingsRepository");
        settingsRepository.updateMasterRepository(repository);
        Mockito.doReturn(coreLibrary).when(syncSettingsServiceHelper).getInstance();
        Mockito.doReturn(true).when(syncConfiguration).resolveSettings();
        Mockito.doReturn(true).when(syncConfiguration).hasExtraSettingsSync();
        Mockito.doReturn(true).when(syncConfiguration).hasGlobalSettings();
        Mockito.doReturn("team-uuid").when(syncConfiguration).getSettingsSyncFilterValue();
        Mockito.doReturn(settings).when(context).allSettings();
        Mockito.doReturn(new ArrayList<Setting>()).when(settings).getUnsyncedSettings();
        Mockito.doReturn(SyncFilter.TEAM).when(syncSettingsServiceHelper).getSettingsSyncFilterParam();

        List<String> params = new ArrayList<>();
        params.add("locationId=location-uuid");

        Mockito.doReturn(params).when(syncConfiguration).getExtraSettingsParameters();
        Mockito.doReturn(new Response<>(ResponseStatus.success, settingsResponse)).when(syncSettingsServiceHelper).getResponse(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(SAMPLE_TEST_TOKEN).when(syncSettingsServiceHelper).getAccessToken();

        int size = syncSettingsServiceHelper.processIntent();

        Assert.assertEquals(3, size);
    }
}
