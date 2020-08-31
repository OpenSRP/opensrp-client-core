package org.smartregister.repository;

import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

import static org.smartregister.AllConstants.CURRENT_LOCALITY;
import static org.smartregister.AllConstants.DEFAULT_LOCALE;
import static org.smartregister.AllConstants.DEFAULT_LOCALITY_ID_PREFIX;
import static org.smartregister.AllConstants.DEFAULT_TEAM_ID_PREFIX;
import static org.smartregister.AllConstants.DEFAULT_TEAM_PREFIX;
import static org.smartregister.AllConstants.DRISHTI_BASE_URL;
import static org.smartregister.AllConstants.ENCRYPTED_GROUP_ID_PREFIX;
import static org.smartregister.AllConstants.ENCRYPTED_PASSWORD_PREFIX;
import static org.smartregister.AllConstants.FORCE_REMOTE_LOGIN;
import static org.smartregister.AllConstants.IS_SYNC_INITIAL_KEY;
import static org.smartregister.AllConstants.IS_SYNC_IN_PROGRESS_PREFERENCE_KEY;
import static org.smartregister.AllConstants.LANGUAGE_PREFERENCE_KEY;
import static org.smartregister.AllConstants.PIONEER_USER;
import static org.smartregister.AllConstants.SERVER_TIMEZONE;
import static org.smartregister.AllConstants.USER_LOCALITY_ID_PREFIX;
import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

public class AllSharedPreferences {
    public static final String ANM_IDENTIFIER_PREFERENCE_KEY = "anmIdentifier";
    private static final String HOST = "HOST";
    private static final String PORT = "PORT";
    private static final String LAST_SYNC_DATE = "LAST_SYNC_DATE";
    private static final String LAST_UPDATED_AT_DATE = "LAST_UPDATED_AT_DATE";
    private static final String LAST_CHECK_TIMESTAMP = "LAST_SYNC_CHECK_TIMESTAMP";
    public final static String LAST_SETTINGS_SYNC_TIMESTAMP = "LAST_SETTINGS_SYNC_TIMESTAMP";
    private static final String LAST_CLIENT_PROCESSED_TIMESTAMP = "LAST_CLIENT_PROCESSED_TIMESTAMP";
    private static final String TRANSACTIONS_KILLED_FLAG = "TRANSACTIONS_KILLED_FLAG";
    private static final String MIGRATED_TO_SQLITE_4 = "MIGRATED_TO_SQLITE_4";
    private static final String PEER_TO_PEER_SYNC_LAST_PROCESSED_RECORD = "PEER_TO_PEER_SYNC_LAST_PROCESSED_RECORD";
    private static final String PEER_TO_PEER_SYNC_LAST_FOREIGN_PROCESSED_RECORD = "PEER_TO_PEER_SYNC_LAST_FOREIGN_PROCESSED_RECORD";
    public static final String MANIFEST_VERSION = "MANIFEST_VERSION";
    public static final String FORMS_VERSION = "FORMS_VERSION";
    private SharedPreferences preferences;

    public AllSharedPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }


    public void updateANMUserName(String userName) {
        preferences.edit().putString(ANM_IDENTIFIER_PREFERENCE_KEY, userName).commit();
    }

    public String fetchRegisteredANM() {
        return preferences.getString(ANM_IDENTIFIER_PREFERENCE_KEY, "").trim();
    }

    public boolean fetchForceRemoteLogin() {
        return preferences.getBoolean(FORCE_REMOTE_LOGIN, true);
    }

    public void saveForceRemoteLogin(boolean forceRemoteLogin) {
        preferences.edit().putBoolean(FORCE_REMOTE_LOGIN, forceRemoteLogin).commit();
    }

    public String fetchServerTimeZone() {
        return preferences.getString(SERVER_TIMEZONE, null);
    }

    public void saveServerTimeZone(String serverTimeZone) {
        preferences.edit().putString(SERVER_TIMEZONE, serverTimeZone).commit();
    }

    public String fetchEncryptedPassword(String username) {
        if (username != null) {
            return preferences.getString(ENCRYPTED_PASSWORD_PREFIX + username, null);
        }
        return null;
    }

    public void saveEncryptedPassword(String username, String password) {
        if (username != null) {
            preferences.edit().putString(ENCRYPTED_PASSWORD_PREFIX + username, password).commit();
        }
    }

    public String fetchPioneerUser() {
        return preferences.getString(PIONEER_USER, null);
    }

    public void savePioneerUser(String username) {
        preferences.edit().putString(PIONEER_USER, username).commit();
    }

    public void saveDefaultLocalityId(String username, String localityId) {
        if (username != null) {
            preferences.edit().putString(DEFAULT_LOCALITY_ID_PREFIX + username, localityId)
                    .commit();
        }
    }

    public String fetchDefaultLocalityId(String username) {
        if (username != null) {
            return preferences.getString(DEFAULT_LOCALITY_ID_PREFIX + username, null);
        }
        return null;
    }

    public void saveUserLocalityId(String username, String localityId) {
        if (username != null) {
            preferences.edit().putString(USER_LOCALITY_ID_PREFIX + username, localityId)
                    .commit();
        }
    }

    public String fetchUserLocalityId(String username) {
        if (username != null) {
            return preferences.getString(USER_LOCALITY_ID_PREFIX + username, null);
        }
        return null;
    }

    public void saveDefaultTeam(String username, String team) {
        if (username != null) {
            preferences.edit().putString(DEFAULT_TEAM_PREFIX + username, team)
                    .commit();
        }
    }

    public String fetchDefaultTeam(String username) {
        if (username != null) {
            return preferences.getString(DEFAULT_TEAM_PREFIX + username, null);
        }
        return null;
    }

    public void saveDefaultTeamId(String username, String teamId) {
        if (username != null) {
            preferences.edit().putString(DEFAULT_TEAM_ID_PREFIX + username, teamId)
                    .commit();
        }
    }

    public String fetchDefaultTeamId(String username) {
        if (username != null) {
            return preferences.getString(DEFAULT_TEAM_ID_PREFIX + username, null);
        }
        return null;
    }

    public String fetchCurrentLocality() {
        return preferences.getString(CURRENT_LOCALITY, null);
    }

    public void saveCurrentLocality(String currentLocality) {
        preferences.edit().putString(CURRENT_LOCALITY, currentLocality).commit();
    }

    public String fetchEncryptedGroupId(String username) {
        if (username != null) {
            return preferences.getString(ENCRYPTED_GROUP_ID_PREFIX + username, null);
        }
        return null;
    }

    public void saveEncryptedGroupId(String username, String groupId) {
        if (username != null) {
            preferences.edit().putString(ENCRYPTED_GROUP_ID_PREFIX + username, groupId).commit();
        }
    }

    public String fetchLanguagePreference() {
        return preferences.getString(LANGUAGE_PREFERENCE_KEY, DEFAULT_LOCALE).trim();
    }

    public void saveLanguagePreference(String languagePreference) {
        preferences.edit().putString(LANGUAGE_PREFERENCE_KEY, languagePreference).commit();
    }

    public Boolean fetchIsSyncInProgress() {
        return preferences.getBoolean(IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, false);
    }

    public void saveIsSyncInProgress(Boolean isSyncInProgress) {
        preferences.edit().putBoolean(IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, isSyncInProgress)
                .commit();
    }

    public String fetchBaseURL(String baseurl) {

        String url = preferences.getString(DRISHTI_BASE_URL, baseurl);
        return StringUtils.isNotBlank(url) && url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

    }

    public String fetchHost(String host) {
        if ((host == null || host.isEmpty()) && preferences.getString(HOST, host).equals(host)) {
            updateUrl(fetchBaseURL(""));
        }
        return preferences.getString(HOST, host);
    }

    public void saveHost(String host) {
        preferences.edit().putString(HOST, host).commit();
    }

    public Integer fetchPort(Integer port) {

        return Integer.parseInt(preferences.getString(PORT, "" + port));
    }

    public Long fetchLastSyncDate(long lastSyncDate) {

        return preferences.getLong(LAST_SYNC_DATE, lastSyncDate);
    }

    public void saveLastSyncDate(long lastSyncDate) {
        preferences.edit().putLong(LAST_SYNC_DATE, lastSyncDate).commit();
    }

    public Long fetchLastUpdatedAtDate(long lastSyncDate) {

        return preferences.getLong(LAST_UPDATED_AT_DATE, lastSyncDate);
    }

    public void saveLastUpdatedAtDate(long lastSyncDate) {
        preferences.edit().putLong(LAST_UPDATED_AT_DATE, lastSyncDate).commit();
    }

    public void savePort(Integer port) {
        preferences.edit().putString(PORT, String.valueOf(port)).commit();
    }

    public void savePreference(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    public String getPreference(String key) {
        return preferences.getString(key, "");
    }

    public void updateUrl(String baseUrl) {
        try {

            URL url = new URL(baseUrl);

            String base = url.getProtocol() + "://" + url.getHost();
            int port = url.getPort();

            logInfo("Base URL: " + base);
            logInfo("Port: " + port);

            saveHost(base);
            savePort(port);

        } catch (MalformedURLException e) {
            logError("Malformed Url: " + baseUrl);
        }
    }

    public void updateANMPreferredName(String userName, String preferredName) {
        preferences.edit().putString(userName, preferredName).commit();
    }

    public String getANMPreferredName(String userName) {
        return preferences.getString(userName, "");
    }

    public void saveIsSyncInitial(boolean initialSynStatus) {
        preferences.edit().putBoolean(IS_SYNC_INITIAL_KEY, initialSynStatus).commit();
    }

    public Boolean fetchIsSyncInitial() {
        return preferences.getBoolean(IS_SYNC_INITIAL_KEY, false);
    }

    public long fetchLastCheckTimeStamp() {
        return preferences.getLong(LAST_CHECK_TIMESTAMP, 0);
    }

    public void updateLastCheckTimeStamp(long lastSyncTimeStamp) {
        preferences.edit().putLong(LAST_CHECK_TIMESTAMP, lastSyncTimeStamp).commit();
    }

    public void updateLastSettingsSyncTimeStamp(long lastSettingsSync) {
        preferences.edit().putLong(LAST_SETTINGS_SYNC_TIMESTAMP, lastSettingsSync).commit();
    }

    public long fetchLastSettingsSyncTimeStamp() {
        return preferences.getLong(LAST_SETTINGS_SYNC_TIMESTAMP, 0);
    }

    public boolean isMigratedToSqlite4() {
        return preferences.getBoolean(MIGRATED_TO_SQLITE_4, false);
    }

    public void setMigratedToSqlite4() {
        preferences.edit().putBoolean(MIGRATED_TO_SQLITE_4, true).commit();
    }

    public int getLastPeerToPeerSyncProcessedEvent() {
        return preferences.getInt(PEER_TO_PEER_SYNC_LAST_PROCESSED_RECORD, -1);
    }

    public void setLastPeerToPeerSyncProcessedEvent(int lastEventRowId) {
        preferences.edit().putInt(PEER_TO_PEER_SYNC_LAST_PROCESSED_RECORD, lastEventRowId).commit();
    }

    public int getLastPeerToPeerSyncForeignProcessedEvent() {
        return preferences.getInt(PEER_TO_PEER_SYNC_LAST_FOREIGN_PROCESSED_RECORD, -1);
    }

    public void setLastPeerToPeerSyncForeignProcessedEvent(int lastEventRowId) {
        preferences.edit().putInt(PEER_TO_PEER_SYNC_LAST_FOREIGN_PROCESSED_RECORD, lastEventRowId).commit();
    }

    public boolean isPeerToPeerUnprocessedEvents() {
        return getLastPeerToPeerSyncProcessedEvent() != -1;
    }

    public void resetLastPeerToPeerSyncProcessedEvent() {
        setLastPeerToPeerSyncProcessedEvent(-1);
    }


    public void updateLastClientProcessedTimeStamp(long lastProcessed) {
        preferences.edit().putLong(LAST_CLIENT_PROCESSED_TIMESTAMP, lastProcessed).commit();
    }

    public long fetchLastClientProcessedTimeStamp() {
        return preferences.getLong(LAST_CLIENT_PROCESSED_TIMESTAMP, 0);
    }


    public void updateTransactionsKilledFlag(boolean transactionsKilled) {
        preferences.edit().putBoolean(TRANSACTIONS_KILLED_FLAG, transactionsKilled).commit();
    }

    public boolean fetchTransactionsKilledFlag() {
        return preferences.getBoolean(TRANSACTIONS_KILLED_FLAG, false);
    }

    public boolean saveManifestVersion(@NonNull String manifestVersion) {
        return preferences.edit().putString(MANIFEST_VERSION, manifestVersion).commit();
    }

    @Nullable
    public String fetchManifestVersion() {
        return preferences.getString(MANIFEST_VERSION, null);
    }

    public boolean saveFormsVersion(@NonNull String formsVersion) {
        return preferences.edit().putString(FORMS_VERSION, formsVersion).commit();
    }

    @Nullable
    public String fetchFormsVersion() {
        return preferences.getString(FORMS_VERSION, null);
    }

    @Nullable
    public SharedPreferences getPreferences() {
        return preferences;
    }
}

