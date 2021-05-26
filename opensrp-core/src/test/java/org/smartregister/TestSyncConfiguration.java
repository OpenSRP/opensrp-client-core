package org.smartregister;

import org.smartregister.view.activity.BaseLoginActivity;

import java.util.List;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 21-04-2020.
 */
public class TestSyncConfiguration extends SyncConfiguration {
    @Override
    public int getSyncMaxRetries() {
        return 3;
    }

    @Override
    public SyncFilter getSyncFilterParam() {
        return SyncFilter.LOCATION;
    }

    @Override
    public String getSyncFilterValue() {
        return "";
    }

    @Override
    public int getUniqueIdSource() {
        return 1;
    }

    @Override
    public int getUniqueIdBatchSize() {
        return 100;
    }

    @Override
    public int getUniqueIdInitialBatchSize() {
        return 250;
    }

    @Override
    public SyncFilter getEncryptionParam() {
        return SyncFilter.TEAM;
    }

    @Override
    public boolean updateClientDetailsTable() {
        return true;
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
        return "opensrp-client-id";
    }

    @Override
    public String getOauthClientSecret() {
        return "$om3cl13nt$3cret";
    }

    @Override
    public Class<? extends BaseLoginActivity> getAuthenticationActivity() {
        return BaseLoginActivity.class;
    }
}
