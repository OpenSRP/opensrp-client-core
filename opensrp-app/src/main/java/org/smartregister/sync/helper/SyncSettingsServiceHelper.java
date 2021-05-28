package org.smartregister.sync.helper;

import androidx.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncFilter;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.account.AccountHelper;
import org.smartregister.domain.Response;
import org.smartregister.domain.Setting;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.intent.SettingsSyncIntentService;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.Utils;

import java.text.MessageFormat;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 14/09/2018.
 */
public class SyncSettingsServiceHelper {

    private HTTPAgent httpAgent;
    private String baseUrl;
    private AllSharedPreferences sharedPreferences;

    public SyncSettingsServiceHelper(String baseUrl, HTTPAgent httpAgent) {

        this.httpAgent = httpAgent;
        this.baseUrl = baseUrl;
        sharedPreferences = getInstance().context().allSharedPreferences();
    }


    public int processIntent() throws JSONException {

        try {
            JSONObject response = pushSettingsToServer();
            if (response != null && response.has(AllConstants.INTENT_KEY.VALIDATED_RECORDS)) {
                JSONArray records = response.getJSONArray(AllConstants.INTENT_KEY.VALIDATED_RECORDS);
                Setting setting;
                for (int i = 0; i < records.length(); i++) {
                    setting = getInstance().context().allSettings().getSetting(records.getString(0));
                    setting.setSyncStatus(SyncStatus.SYNCED.name());
                    getInstance().context().allSettings().putSetting(setting);
                }
            }

        } catch (JSONException e) {
            Timber.e(e);
        }

        JSONArray settings = getSettings();
        if (settings != null && settings.length() > 0) {
            ServerSettingsHelper.saveSetting(settings);
        }

        return settings == null ? 0 : settings.length();
    }

    private JSONArray getSettings() throws JSONException {

        String authToken = getAccessToken();

        JSONArray settings = pullSettingsFromServer(getInstance().getSyncConfiguration().getSettingsSyncFilterValue(), authToken);
        getGlobalSettings(settings, authToken);
        getExtraSettings(settings, authToken);
        return settings;
    }

    @VisibleForTesting
    protected String getAccessToken() {
        AccountAuthenticatorXml authenticatorXml = CoreLibrary.getInstance().getAccountAuthenticatorXml();
        return AccountHelper.getCachedOAuthToken(sharedPreferences.fetchRegisteredANM(), authenticatorXml.getAccountType(), AccountHelper.TOKEN_TYPE.PROVIDER);
    }

    @VisibleForTesting
    protected CoreLibrary getInstance() {
        return CoreLibrary.getInstance();
    }

    // will automatically use the resolve check
    private void getExtraSettings(JSONArray settings, String accessToken) throws JSONException {
        JSONArray completeExtraSettings = new JSONArray();
        if (getInstance().getSyncConfiguration().hasExtraSettingsSync()) {
            List<String> syncParams = getInstance().getSyncConfiguration().getExtraSettingsParameters();
            if (syncParams.size() > 0) {
                for (String params : syncParams) {
                    String url = SettingsSyncIntentService.SETTINGS_URL + "?" + params + "&" + AllConstants.SERVER_VERSION + "=" + sharedPreferences.fetchLastSettingsSyncTimeStamp() + "&" + AllConstants.RESOLVE + "=" + getInstance().getSyncConfiguration().resolveSettings();
                    JSONArray extraSettings = pullSettings(url, accessToken);
                    if (extraSettings != null) {
                        aggregateSettings(completeExtraSettings, extraSettings);
                    }
                }

                aggregateSettings(settings, completeExtraSettings);
            }
        }
    }

    private void getGlobalSettings(JSONArray settings, String accessToken) throws JSONException {
        JSONArray globalSettings = new JSONArray();
        if (getInstance().getSyncConfiguration().hasGlobalSettings()) {
            globalSettings = pullGlobalSettingsFromServer(accessToken);
        }

        aggregateSettings(settings, globalSettings);
    }

    private void aggregateSettings(JSONArray settings, JSONArray globalSettings) throws JSONException {
        if (!JsonFormUtils.isBlankJsonArray(globalSettings) && settings != null) {
            for (int i = 0; i < globalSettings.length(); i++) {
                JSONObject global = globalSettings.getJSONObject(i);
                settings.put(global);
            }
        }
    }

    private JSONObject pushSettingsToServer() throws JSONException {
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = MessageFormat.format("{0}/{1}", baseUrl, SettingsSyncIntentService.SETTINGS_URL);
        Timber.i("URL: %s", url);

        if (httpAgent == null) {
            Timber.e("%s http agent is null", url);
            return null;
        }

        JSONObject payload = createSettingsConfigurationPayload();

        if (payload.getJSONArray(AllConstants.INTENT_KEY.SETTING_CONFIGURATIONS).length() > 0) {

            Response<String> response = httpAgent.postWithJsonResponse(url, payload.toString());

            return new JSONObject(response.payload());

        } else return null;
    }

    /**
     * Create a sync setting url that allows the users to get non adapted settings from the server
     * Get the resolved settings from the server.
     *
     * @param syncFilterValue {@link String} -- the set sync filter value
     * @return settings {@link JSONArray} -- a JSON array of all the settings
     * @throws JSONException
     */
    public JSONArray pullSettingsFromServer(String syncFilterValue, String accessToken) throws JSONException {
        String url = SettingsSyncIntentService.SETTINGS_URL + "?" + getSettingsSyncFilterParam().value() + "=" + syncFilterValue + "&" + AllConstants.SERVER_VERSION + "=" + sharedPreferences.fetchLastSettingsSyncTimeStamp();
        return pullSettings(url, accessToken);
    }

    @VisibleForTesting
    protected SyncFilter getSettingsSyncFilterParam() {
        return getInstance().getSyncConfiguration().getSettingsSyncFilterParam();
    }

    @VisibleForTesting
    protected String getGlobalSettingsQueryParams() {
        return Utils.composeApiCallParamsString(getInstance().getSyncConfiguration().getGlobalSettingsQueryParams());
    }

    /**
     * Gets settings that are not tied to team,teamid,location,provider
     *
     * @return settings {@link JSONArray} -- a JSON array of all the settings
     * @throws JSONException
     */
    public JSONArray pullGlobalSettingsFromServer(String accessToken) throws JSONException {
        String url = SettingsSyncIntentService.SETTINGS_URL + "?" + AllConstants.SERVER_VERSION + "=" + sharedPreferences.fetchLastSettingsSyncTimeStamp() + getGlobalSettingsQueryParams();
        return pullSettings(url, accessToken);
    }


    private JSONObject createSettingsConfigurationPayload() throws JSONException {
        JSONObject siteSettingsPayload = new JSONObject();
        JSONArray settingsArray = new JSONArray();
        List<Setting> unsyncedSettings = getInstance().context().allSettings().getUnsyncedSettings();

        for (int i = 0; i < unsyncedSettings.size(); i++) {
            SyncableJSONObject settingsWrapper = new SyncableJSONObject(unsyncedSettings.get(i).getValue());
            settingsArray.put(settingsWrapper);
        }

        siteSettingsPayload.put(AllConstants.INTENT_KEY.SETTING_CONFIGURATIONS, settingsArray);
        return siteSettingsPayload;
    }

    private JSONArray pullSettings(String directoryUrl, String accessToken) throws JSONException {
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String completeUrl = baseUrl + directoryUrl;

        Timber.i("URL: %s", completeUrl);

        if (httpAgent == null) {
            Timber.e("%s http agent is null", completeUrl);
            return null;
        }

        Response resp = getResponse(completeUrl, accessToken);

        if (resp == null || resp.isFailure()) {
            Timber.e(" %s  not returned data ", completeUrl);
            return null;
        }

        return new JSONArray((String) resp.payload());
    }

    @VisibleForTesting
    protected Response<String> getResponse(String completeUrl, String accessToken) {
        return httpAgent.fetchWithCredentials(completeUrl, accessToken);
    }
}
