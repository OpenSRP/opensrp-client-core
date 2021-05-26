package org.smartregister.repository;

import org.smartregister.domain.Setting;

import java.util.List;

public class AllSettings {
    public static final String APPLIED_VILLAGE_FILTER_SETTING_KEY = "appliedVillageFilter";
    public static final String PREVIOUS_FETCH_INDEX_SETTING_KEY = "previousFetchIndex";
    public static final String PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY = "previousFormSyncIndex";
    private static final String ANM_LOCATION = "anmLocation";
    private static final String ANM_TEAM = "anmTeam";
    private static final String USER_INFORMATION = "userInformation";

    protected AllSharedPreferences preferences;
    protected SettingsRepository settingsRepository;

    public AllSettings(AllSharedPreferences preferences, SettingsRepository settingsRepository) {
        this.preferences = preferences;
        this.settingsRepository = settingsRepository;
    }

    public void registerANM(String userName) {
        preferences.updateANMUserName(userName);
    }

    public void savePreviousFetchIndex(String value) {
        settingsRepository.updateSetting(PREVIOUS_FETCH_INDEX_SETTING_KEY, value);
    }

    public String fetchPreviousFetchIndex() {
        return settingsRepository.querySetting(PREVIOUS_FETCH_INDEX_SETTING_KEY, "0");
    }

    public void saveAppliedVillageFilter(String village) {
        settingsRepository.updateSetting(APPLIED_VILLAGE_FILTER_SETTING_KEY, village);
    }

    public String appliedVillageFilter(String defaultFilterValue) {
        return settingsRepository
                .querySetting(APPLIED_VILLAGE_FILTER_SETTING_KEY, defaultFilterValue);
    }

    public String fetchPreviousFormSyncIndex() {
        return settingsRepository.querySetting(PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY, "0");
    }

    public void savePreviousFormSyncIndex(String value) {
        settingsRepository.updateSetting(PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY, value);
    }

    public void saveANMLocation(String anmLocation) {
        settingsRepository.updateSetting(ANM_LOCATION, anmLocation);
    }

    public void saveANMTeam(String anmTeam) {
        settingsRepository.updateSetting(ANM_TEAM, anmTeam);
    }

    public String fetchANMLocation() {
        return settingsRepository.querySetting(ANM_LOCATION, "");
    }

    public void saveUserInformation(String userInformation) {
        settingsRepository.updateSetting(USER_INFORMATION, userInformation);
    }

    public String fetchUserInformation() {
        return settingsRepository.querySetting(USER_INFORMATION, "");
    }

    public void put(String key, String value) {
        settingsRepository.updateSetting(key, value);
    }

    public String get(String key) {
        return get(key, null);
    }

    public String get(String key, String defaultValue) {
        return settingsRepository.querySetting(key, defaultValue);
    }

    public Setting getSetting(String key) {
        return settingsRepository.querySetting(key);
    }

    public List<Setting> getSettingsByType(String type) {
        return settingsRepository.querySettingsByType(type);
    }

    public void putSetting(Setting setting) {
        settingsRepository.updateSetting(setting);
    }

    public List<Setting> getUnsyncedSettings() {
        return settingsRepository.queryUnsyncedSettings();
    }

    public int getUnsyncedSettingsCount() {
        return settingsRepository.queryUnsyncedSettingsCount();
    }

    public String fetchRegisteredANM() {
        return preferences.fetchRegisteredANM();
    }

    public String fetchDefaultTeamId(String username) {
        return preferences.fetchDefaultTeamId(username);
    }

    public String fetchDefaultTeam(String username) {
        return preferences.fetchDefaultTeam(username);
    }

    public String fetchDefaultLocalityId(String username) {
        return preferences.fetchDefaultLocalityId(username);
    }

    public AllSharedPreferences getPreferences() {
        return preferences;
    }
}
