package org.smartregister.repository;

import android.content.SharedPreferences;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.AllConstants;
import org.smartregister.sync.mock.MockEditor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.smartregister.AllConstants.CAMPAIGNS;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27)
public class AllSharedPreferencesTest extends TestCase {
    @Mock
    private SharedPreferences preferences;
    private static final String HOST = "HOST";
    private static final String PORT = "PORT";
    private static final String USERNAME = "USERNAME";
    AllSharedPreferences allSharedPreferences;
    private final String str = "default";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        allSharedPreferences = new AllSharedPreferences(preferences);
        Mockito.when(preferences.getString(Mockito.anyString(), Mockito.isNull(String.class))).thenReturn(str);
        Mockito.when(preferences.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(str);
        Mockito.when(preferences.getLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(0l);
        Mockito.when(preferences.getString(HOST, "")).thenReturn("");
        Mockito.when(preferences.getString(PORT, "80")).thenReturn("8080");
        Mockito.when(preferences.getString(AllConstants.DRISHTI_BASE_URL, "")).thenReturn("http://www.google.com");
        Mockito.when(preferences.edit()).thenReturn(MockEditor.getEditor());
    }

    @After
    public void tearDown() {
        allSharedPreferences = null;
    }

    @Test
    public void assertupdateANMUserNameCallsPreferenceEdit() {
        allSharedPreferences.updateANMUserName("");
        Mockito.verify(preferences, Mockito.times(2)).edit();
    }

    @Test
    public void assertFetchForceRemoteLogin() {
        Mockito.when(preferences.getBoolean(AllConstants.FORCE_REMOTE_LOGIN + "_" + USERNAME, true)).thenReturn(true);
        Assert.assertEquals(allSharedPreferences.fetchForceRemoteLogin(USERNAME), true);
    }

    @Test
    public void assertFetchServerTimeZone() {
        Assert.assertEquals(str, allSharedPreferences.fetchServerTimeZone());
    }

    @Test
    public void assertSaveServerTimeZone() {
        allSharedPreferences.saveServerTimeZone("");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertFetchPioneerUser() {
        Assert.assertEquals(allSharedPreferences.fetchPioneerUser(), str);
    }

    @Test
    public void assertfetchLastUpdatedAtDate() {
        Assert.assertEquals(allSharedPreferences.fetchLastUpdatedAtDate(0l), new Long(0l));
    }

    @Test
    public void assertsaveLastUpdatedAtDate() {
        allSharedPreferences.saveLastUpdatedAtDate(0l);
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertfetchCurrentLocality() {
        Assert.assertEquals(allSharedPreferences.fetchCurrentLocality(), str);
    }

    @Test
    public void assertsaveCurrentLocality() {
        allSharedPreferences.saveCurrentLocality("LoCaLiTy");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertfetchCurrentDataStrategy() {
        Assert.assertEquals(allSharedPreferences.fetchCurrentDataStrategy(), str);
    }

    @Test
    public void assertsaveCurrentDataStrategy() {
        allSharedPreferences.saveCurrentDataStrategy("Mobile Clinic");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertsaveLanguagePreference() {
        allSharedPreferences.saveLanguagePreference("EN");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertfetchBaseURL() {
        Assert.assertEquals(allSharedPreferences.fetchBaseURL(""), "http://www.google.com");
    }

    @Test
    public void assertsavePreference() {
        allSharedPreferences.savePreference("", "");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertgetPreference() {
        Assert.assertEquals(allSharedPreferences.getPreference(""), str);
    }

    @Test
    public void assertupdateANMPreferredName() {
        allSharedPreferences.updateANMPreferredName("", "");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertgetANMPreferredName() {
        org.junit.Assert.assertEquals(allSharedPreferences.getANMPreferredName("uname"), str);
    }


    @Test
    public void assertfetchHost() {
        Assert.assertEquals(allSharedPreferences.fetchHost(""), "");
        Assert.assertEquals(allSharedPreferences.fetchHost(null), str);
        Mockito.when(preferences.getString(AllConstants.DRISHTI_BASE_URL, "")).thenReturn("Malformed Url");
        Assert.assertEquals(allSharedPreferences.fetchHost(""), "");
    }

    @Test
    public void assertfetchPort() {
        Assert.assertEquals(allSharedPreferences.fetchPort(new Integer(80)), new Integer(8080));
    }

    @Test
    public void assertSavePioneerUser() {
        allSharedPreferences.savePioneerUser("uname");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertSaveDefaultLocalityId() {
        allSharedPreferences.saveDefaultLocalityId("uname", "Id");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertFetchDefaultLocalityId() {
        org.junit.Assert.assertNull(allSharedPreferences.fetchDefaultLocalityId(null));
        org.junit.Assert.assertEquals(allSharedPreferences.fetchDefaultLocalityId(""), str);
    }

    @Test
    public void assertSaveForceRemoteLogin() {
        allSharedPreferences.saveForceRemoteLogin(true, USERNAME);
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void shouldFetchANMIdentifierFromPreferences() throws Exception {
        Mockito.when(preferences.getString("anmIdentifier", "")).thenReturn("1234");

        String actual = allSharedPreferences.fetchRegisteredANM();

        Mockito.verify(preferences).getString("anmIdentifier", "");
        assertEquals("1234", actual);
    }

    @Test
    public void shouldTrimANMIdentifier() throws Exception {
        Mockito.when(preferences.getString("anmIdentifier", "")).thenReturn("  1234 ");

        String actual = allSharedPreferences.fetchRegisteredANM();

        Mockito.verify(preferences).getString("anmIdentifier", "");
        assertEquals("1234", actual);
    }

    @Test
    public void shouldFetchLanguagePreference() throws Exception {
        Mockito.when(preferences.getString(AllConstants.LANGUAGE_PREFERENCE_KEY, AllConstants.DEFAULT_LOCALE)).thenReturn(AllConstants.ENGLISH_LANGUAGE);

        assertEquals("English", allSharedPreferences.fetchLanguagePreference());
    }

    @Test
    public void shouldFetchIsSyncInProgress() throws Exception {
        Mockito.when(preferences.getBoolean(AllConstants.IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, false)).thenReturn(true);

        assertTrue(allSharedPreferences.fetchIsSyncInProgress());
    }

    @Test
    public void shouldSaveIsSyncInProgress() throws Exception {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putBoolean(AllConstants.IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, true)).thenReturn(editor);

        allSharedPreferences.saveIsSyncInProgress(true);

        Mockito.verify(editor).putBoolean(AllConstants.IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, true);
        Mockito.verify(editor).commit();
    }

    @Test
    public void shouldSaveCampaignsOperationalArea() {
        allSharedPreferences.savePreference(CAMPAIGNS, "Miti Rural Health Centre");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void shouldGetCampaignsOperationalArea() {
        Assert.assertEquals(allSharedPreferences.getPreference(CAMPAIGNS), str);
    }

    @Test
    public void assertSaveIsSyncInitialIsFalse() {
        Assert.assertFalse(allSharedPreferences.fetchIsSyncInitial());
    }

    @Test
    public void testSaveUserLocalityId() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putString(any(), any())).thenReturn(editor);

        allSharedPreferences.saveUserLocalityId("uname", "Id");

        Mockito.verify(editor).putString(any(), any());
        Mockito.verify(editor).commit();
    }

    @Test
    public void testFetchUserLocalityId() {
        Mockito.when(preferences.getString(any(), any())).thenReturn("local-id");

        assertEquals("local-id", allSharedPreferences.fetchUserLocalityId("uname"));
        assertNull(allSharedPreferences.fetchUserLocalityId(null));
    }

    @Test
    public void testSaveDefaultTeam() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putString(any(), any())).thenReturn(editor);

        allSharedPreferences.saveDefaultTeam("uname", "team");

        Mockito.verify(editor).putString(any(), any());
        Mockito.verify(editor).commit();
    }

    @Test
    public void testFetchDefaultTeam() {
        Mockito.when(preferences.getString(any(), any())).thenReturn("team");

        assertEquals("team", allSharedPreferences.fetchDefaultTeam("uname"));
        assertNull(allSharedPreferences.fetchDefaultTeam(null));
    }

    @Test
    public void testFetchDefaultTeamId() {
        Mockito.when(preferences.getString(any(), any())).thenReturn("team-id");

        assertEquals("team-id", allSharedPreferences.fetchDefaultTeamId("uname"));
        assertNull(allSharedPreferences.fetchDefaultTeamId(null));
    }

    @Test
    public void testFetchLastSyncDate() {
        Mockito.when(preferences.getLong(any(), anyLong())).thenReturn(2000L);

        assertEquals((Long) 2000L, allSharedPreferences.fetchLastSyncDate(1000L));
    }

    @Test
    public void testSaveLastSyncDate() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putLong(any(), anyLong())).thenReturn(editor);

        allSharedPreferences.saveLastSyncDate(2000L);

        Mockito.verify(editor).putLong(any(), anyLong());
        Mockito.verify(editor).commit();
    }

    @Test
    public void testSaveIsSyncInitial() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putBoolean(any(), anyBoolean())).thenReturn(editor);

        allSharedPreferences.saveIsSyncInitial(true);

        Mockito.verify(editor).putBoolean(any(), anyBoolean());
        Mockito.verify(editor).commit();
    }

    @Test
    public void testFetchLastCheckTimeStamp() {
        Mockito.when(preferences.getLong(any(), anyLong())).thenReturn(2000L);

        assertEquals(2000L, allSharedPreferences.fetchLastCheckTimeStamp());
    }

    @Test
    public void testUpdateLastCheckTimeStamp() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putLong(any(), anyLong())).thenReturn(editor);

        allSharedPreferences.updateLastCheckTimeStamp(1000L);

        Mockito.verify(editor).putLong(any(), anyLong());
        Mockito.verify(editor).commit();
    }

    @Test
    public void testUpdateLastSettingsSyncTimeStamp() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putLong(any(), anyLong())).thenReturn(editor);

        allSharedPreferences.updateLastSettingsSyncTimeStamp(1000L);

        Mockito.verify(editor).putLong(any(), anyLong());
        Mockito.verify(editor).commit();
    }

    @Test
    public void testFetchLastSettingsSyncTimeStamp() {
        Mockito.when(preferences.getLong(any(), anyLong())).thenReturn(2000L);

        assertEquals(2000L, allSharedPreferences.fetchLastSettingsSyncTimeStamp());
    }

    @Test
    public void testIsMigratedToSqlite4() {
        Mockito.when(preferences.getBoolean(any(), anyBoolean())).thenReturn(true);

        assertTrue(allSharedPreferences.isMigratedToSqlite4());
    }

    @Test
    public void testSetMigratedToSqlite4() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putBoolean(any(), anyBoolean())).thenReturn(editor);

        allSharedPreferences.setMigratedToSqlite4();

        Mockito.verify(editor).putBoolean(any(), anyBoolean());
        Mockito.verify(editor).commit();
    }

    @Test
    public void testGetLastPeerToPeerSyncProcessedEvent() {
        Mockito.when(preferences.getInt(any(), anyInt())).thenReturn(10);

        assertEquals(10, allSharedPreferences.getLastPeerToPeerSyncProcessedEvent());
    }

    @Test
    public void testSetLastPeerToPeerSyncProcessedEvent() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putInt(any(), anyInt())).thenReturn(editor);

        allSharedPreferences.setLastPeerToPeerSyncProcessedEvent(10);

        Mockito.verify(editor).putInt(any(), anyInt());
        Mockito.verify(editor).commit();
    }

    @Test
    public void isPeerToPeerUnprocessedEvents() {
        Mockito.when(preferences.getBoolean(any(), anyBoolean())).thenReturn(true);

        assertTrue(allSharedPreferences.isPeerToPeerUnprocessedEvents());
    }

    @Test
    public void resetLastPeerToPeerSyncProcessedEvent() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putInt(any(), anyInt())).thenReturn(editor);

        allSharedPreferences.resetLastPeerToPeerSyncProcessedEvent();

        Mockito.verify(editor).putInt(any(), anyInt());
        Mockito.verify(editor).commit();
    }

    @Test
    public void testUpdateLastClientProcessedTimeStamp() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putLong(any(), anyLong())).thenReturn(editor);

        allSharedPreferences.updateLastClientProcessedTimeStamp(1000L);

        Mockito.verify(editor).putLong(any(), anyLong());
        Mockito.verify(editor).commit();
    }

    @Test
    public void testFetchLastClientProcessedTimeStamp() {
        Mockito.when(preferences.getLong(any(), anyLong())).thenReturn(2000L);

        assertEquals(2000L, allSharedPreferences.fetchLastClientProcessedTimeStamp());
    }

    @Test
    public void testUpdateTransactionsKilledFlag() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putBoolean(any(), anyBoolean())).thenReturn(editor);

        allSharedPreferences.updateTransactionsKilledFlag(true);

        Mockito.verify(editor).putBoolean(any(), anyBoolean());
        Mockito.verify(editor).commit();
    }

    @Test
    public void testFetchTransactionsKilledFlag() {
        Mockito.when(preferences.getBoolean(any(), anyBoolean())).thenReturn(true);

        assertTrue(allSharedPreferences.fetchTransactionsKilledFlag());
    }
}
