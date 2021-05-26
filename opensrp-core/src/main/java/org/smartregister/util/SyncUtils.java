package org.smartregister.util;

import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.account.AccountHelper;
import org.smartregister.domain.Setting;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.BaseRepository;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.AllConstants.ACCOUNT_DISABLED;
import static org.smartregister.AllConstants.FORCED_LOGOUT.MIN_ALLOWED_APP_VERSION;
import static org.smartregister.AllConstants.FORCED_LOGOUT.MIN_ALLOWED_APP_VERSION_SETTING;
import static org.smartregister.AllConstants.JSON.KEY;
import static org.smartregister.AllConstants.JSON.VALUE;
import static org.smartregister.AllConstants.SETTINGS;
import static org.smartregister.util.Utils.getVersionCode;
import static org.smartregister.util.Utils.isEmptyCollection;

/**
 * Created by samuelgithengi on 1/28/19.
 */
public class SyncUtils {

    private org.smartregister.Context opensrpContext = CoreLibrary.getInstance().context();

    private Context context;

    public SyncUtils(Context context) {
        this.context = context;
    }

    public boolean verifyAuthorization() {
        return CoreLibrary.getInstance().context().getHttpAgent().verifyAuthorization();
    }

    public void logoutUser() throws AuthenticatorException, OperationCanceledException, IOException {
        logoutUser(R.string.account_disabled_logged_off);
    }

    public void logoutUser(@StringRes int logoutMessage) throws AuthenticatorException, OperationCanceledException, IOException {
        //force remote login
        opensrpContext.userService().forceRemoteLogin(opensrpContext.allSharedPreferences().fetchRegisteredANM());

        Intent logoutUserIntent = getLogoutUserIntent(logoutMessage);

        AccountManagerFuture<Bundle> reAuthenticateFuture = AccountHelper.reAuthenticateUserAfterSessionExpired(opensrpContext.allSharedPreferences().fetchRegisteredANM(), CoreLibrary.getInstance().getAccountAuthenticatorXml().getAccountType(), AccountHelper.TOKEN_TYPE.PROVIDER);
        Intent accountAuthenticatorIntent = reAuthenticateFuture.getResult().getParcelable(AccountManager.KEY_INTENT);
        accountAuthenticatorIntent.putExtras(logoutUserIntent);
        context.startActivity(logoutUserIntent);

        //logoff opensrp session
        opensrpContext.userService().logoutSession();
    }

    @VisibleForTesting
    @NonNull
    protected Intent getLogoutUserIntent(@StringRes int logoutMessage) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(context.getPackageName());

        //retrieve the main/launcher activity defined in the manifest and open it
        List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(intent, 0);
        if (!isEmptyCollection(activities)) {
            intent = intent.setClassName(context.getPackageName(), activities.get(0).activityInfo.name);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ACCOUNT_DISABLED, context.getString(logoutMessage));
        }

        return intent;
    }

    public boolean isAppVersionAllowed() {
        boolean isAppVersionAllowed = true;
        try {

            // see if setting was synced
            AllSettings settingsRepository = opensrpContext.allSettings();
            Setting rawMinAllowedAppVersionSetting;
            try {
                rawMinAllowedAppVersionSetting = settingsRepository.getSetting(MIN_ALLOWED_APP_VERSION_SETTING);
            } catch (NullPointerException e) {
                Timber.e(e);
                return true;
            }
            if (rawMinAllowedAppVersionSetting == null) {
                return true;
            }

            // if min version is already extracted
            Setting extractedMinAllowedAppVersionSetting = settingsRepository.getSetting(MIN_ALLOWED_APP_VERSION);
            if (extractedMinAllowedAppVersionSetting != null
                    && isNewerSetting(extractedMinAllowedAppVersionSetting, rawMinAllowedAppVersionSetting)) {
                return !isOutdatedVersion(Long.parseLong(extractedMinAllowedAppVersionSetting.getValue()));
            }

            // else, attempt to extract it
            int minAllowedAppVersion = extractMinAllowedAppVersion(rawMinAllowedAppVersionSetting.getValue());
            if (isOutdatedVersion(minAllowedAppVersion)) {
                isAppVersionAllowed = false;
            }

            // update settings repository with the extracted version of the min allowed setting
            extractedMinAllowedAppVersionSetting = new Setting();
            extractedMinAllowedAppVersionSetting.setValue(String.valueOf(minAllowedAppVersion));
            extractedMinAllowedAppVersionSetting.setKey(MIN_ALLOWED_APP_VERSION);
            extractedMinAllowedAppVersionSetting.setSyncStatus(BaseRepository.TYPE_Synced);
            extractedMinAllowedAppVersionSetting.setIdentifier(MIN_ALLOWED_APP_VERSION);
            extractedMinAllowedAppVersionSetting.setVersion(getIncrementedServerVersion(rawMinAllowedAppVersionSetting));
            settingsRepository.putSetting(extractedMinAllowedAppVersionSetting);
        } catch (Exception e) {
            Timber.e(e);
        }
        return isAppVersionAllowed;
    }

    /**
     * Returns the number of times sync should be attempted
     *
     * @return
     */
    public int getNumOfSyncAttempts() {
        int numOfRetries = CoreLibrary.getInstance().getSyncConfiguration().getSyncMaxRetries();
        return  numOfRetries > 0 ? numOfRetries + 1 : 1;
    }

    /**
     * Returns true if {@param setting1} was updated more recently when compared to {@param setting2},
     * and false otherwise
     *
     * @param setting1
     * @param setting2
     * @return
     */
    private boolean isNewerSetting(Setting setting1, Setting setting2) {
        return Long.valueOf(setting1.getVersion()) > Long.valueOf(setting2.getVersion());
    }

    private synchronized String getIncrementedServerVersion(Setting setting) {
        if (setting == null || StringUtils.isBlank(setting.getVersion())) {
            return null;
        }
        return String.valueOf(Long.valueOf(setting.getVersion()) + 1);
    }

    private boolean isOutdatedVersion(long minAllowedAppVersion) throws PackageManager.NameNotFoundException {
        return getVersionCode(context) < minAllowedAppVersion;
    }

    private int extractMinAllowedAppVersion(String setting) {
        int minAllowedAppVersion = 0;
        try {
            JSONArray settings = new JSONObject(setting).optJSONArray(SETTINGS);
            for (int i = 0; i < settings.length(); i++) {
                JSONObject currSettingObj = settings.optJSONObject(i);
                String currKey = currSettingObj.optString(KEY);
                if (MIN_ALLOWED_APP_VERSION_SETTING.equals(currKey)) {
                    minAllowedAppVersion = currSettingObj.optInt(VALUE, minAllowedAppVersion);
                    break;
                }
            }
        } catch (NumberFormatException e) {
            Timber.e(e, "Please ensure that the min app version is an integer");
        } catch (JSONException e) {
            Timber.e(e);
        }
        return minAllowedAppVersion;
    }
}
