package org.smartregister.sample.application;

import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sample.BuildConfig;
import org.smartregister.view.activity.BaseLoginActivity;

import java.util.List;

/**
 * Created by ndegwamartin on 06/05/2020.
 */
public class SampleSyncConfiguration extends SyncConfiguration {
    @Override
    public int getSyncMaxRetries() {
        return 0;
    }

    @Override
    public SyncFilter getSyncFilterParam() {
        return SyncFilter.LOCATION;
    }

    @Override
    public String getSyncFilterValue() {
        AllSharedPreferences sharedPreferences = CoreLibrary.getInstance().context().userService().getAllSharedPreferences();
        return sharedPreferences.fetchDefaultLocalityId(sharedPreferences.fetchRegisteredANM());
    }

    @Override
    public int getUniqueIdSource() {
        return 0;
    }

    @Override
    public int getUniqueIdBatchSize() {
        return 0;
    }

    @Override
    public int getUniqueIdInitialBatchSize() {
        return 0;
    }

    @Override
    public SyncFilter getEncryptionParam() {
        return SyncFilter.TEAM;
    }

    @Override
    public boolean updateClientDetailsTable() {
        return false;
    }

    @Override
    public List<String> getSynchronizedLocationTags() {
        return null;
    }

    @Override
    public String getTopAllowedLocationLevel() {
        return null;
    }

    @Override
    public String getOauthClientId() {
        return BuildConfig.OAUTH_CLIENT_ID;
    }

    @Override
    public String getOauthClientSecret() {
        return BuildConfig.OAUTH_CLIENT_SECRET;
    }

    @Override
    public Class<? extends BaseLoginActivity> getAuthenticationActivity() {
        return null;
    }
}
