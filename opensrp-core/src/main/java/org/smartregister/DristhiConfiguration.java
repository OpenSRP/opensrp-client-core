package org.smartregister;

import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.IntegerUtil;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Properties;

public class DristhiConfiguration {

    public static final String TAG = "DristhiConfiguration";
    protected static final String DRISHTI_BASE_URL = "DRISHTI_BASE_URL";

    protected static final String HOST = "HOST";
    protected static final String PORT = "PORT";
    protected static final String SHOULD_VERIFY_CERTIFICATE = "SHOULD_VERIFY_CERTIFICATE";
    protected static final String SYNC_DOWNLOAD_BATCH_SIZE = "SYNC_DOWNLOAD_BATCH_SIZE";
    protected static final String APP_NAME = "APP_NAME";
    protected static final String SYNC_FORM = "SYNC_FORM";
    protected static AllSharedPreferences preferences;
    protected Properties properties;
    protected String dummyData = null;

    public DristhiConfiguration() {
        preferences = CoreLibrary.getInstance().context().allSharedPreferences();
        properties = CoreLibrary.getInstance().context().getAppProperties();
    }

    public String getDummyData() {
        return this.dummyData;
    }

    private String get(String key) {
        return properties.getProperty(key);
    }

    public String host() {

        return this.get(HOST);

    }

    public int port() {

        return preferences.fetchPort(Integer.parseInt(this.get(PORT)));
    }

    public boolean shouldVerifyCertificate() {
        return Boolean.parseBoolean(this.get(SHOULD_VERIFY_CERTIFICATE));
    }

    public String dristhiBaseURL() {

        return preferences.fetchBaseURL(this.get(AllConstants.DRISHTI_BASE_URL));
    }

    public int syncDownloadBatchSize() {
        return IntegerUtil.tryParse(this.get(SYNC_DOWNLOAD_BATCH_SIZE), 100);
    }

    public String appName() {
        return this.get(APP_NAME) != null ? this.get(APP_NAME) : "";
    }

    public boolean shouldSyncForm() {
        return this.get(SYNC_FORM) != null && Boolean.parseBoolean(this.get(SYNC_FORM));
    }

    public DrishtiApplication getDrishtiApplication() {
        return DrishtiApplication.getInstance();
    }

}
