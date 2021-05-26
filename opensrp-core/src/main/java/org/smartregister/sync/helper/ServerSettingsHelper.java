package org.smartregister.sync.helper;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.ServerSetting;
import org.smartregister.domain.Setting;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 13/08/2018.
 */
public class ServerSettingsHelper {

    public static List<ServerSetting> serverSettings;

    private static final Type SERVER_SETTING_TYPE = new TypeToken<List<ServerSetting>>() {
    }.getType();

    public ServerSettingsHelper(String key) {
        initializeServerSettings(key);

    }

    public List<ServerSetting> getServerSettings() {
        return serverSettings;
    }

    public static List<ServerSetting> fetchServerSettingsByTypeKey(String typeKey) {
        try {
            Gson gson = new Gson();
            Setting serverSetting = CoreLibrary.getInstance().context().allSettings().getSetting(typeKey);

            JSONObject settingObject = serverSetting != null ? new JSONObject(serverSetting.getValue()) : null;
            JSONArray settingArray = new JSONArray();

            if (serverSetting != null && settingObject.has(AllConstants.SETTINGS)) {
                settingArray = settingObject.getJSONArray(AllConstants.SETTINGS);
            }

            return gson.fromJson(settingArray.toString(), SERVER_SETTING_TYPE); // contains the whole reviews list

        } catch (Exception e) {
            Timber.e(e);
            return new ArrayList<>();
        }
    }

    public static JSONArray saveSetting(JSONArray serverSettings) throws JSONException {
        for (int i = serverSettings.length() - 1; i >= 0; i--) {

            JSONObject jsonObject = serverSettings.getJSONObject(i);
            Setting serverSetting = new Setting();
            serverSetting.setKey(jsonObject.getString(AllConstants.IDENTIFIER));
            serverSetting.setValue(jsonObject.toString());
            serverSetting.setVersion(jsonObject.optString(AllConstants.SERVER_VERSION));
            serverSetting.setSyncStatus(SyncStatus.SYNCED.name());


            CoreLibrary.getInstance().context().allSharedPreferences().updateLastSettingsSyncTimeStamp(!TextUtils.isEmpty(serverSetting.getVersion()) ? Long.valueOf(serverSetting.getVersion()) : 0l);

            CoreLibrary.getInstance().context().allSettings().put(AllSharedPreferences.LAST_SETTINGS_SYNC_TIMESTAMP, serverSetting.getVersion());
            CoreLibrary.getInstance().context().allSettings().putSetting(serverSetting);

        }

        return serverSettings;
    }

    private static void initializeServerSettings(String key) {

        serverSettings = ServerSettingsHelper.fetchServerSettingsByTypeKey(key);
    }
}
