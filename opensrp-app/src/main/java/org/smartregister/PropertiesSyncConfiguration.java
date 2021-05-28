package org.smartregister;

import org.smartregister.domain.Environment;

/**
 * Default implementation of SyncConfiguration for handling OAuthClient from local.properties file
 */
public abstract class PropertiesSyncConfiguration extends SyncConfiguration {

    private final EnvironmentManager environmentManager;

    public PropertiesSyncConfiguration(EnvironmentManager environmentManager) {
        this.environmentManager = environmentManager;
    }

    @Override
    public String getOauthClientId() {
        return getCurrentEnvironment().getId();
    }

    @Override
    public String getOauthClientSecret() {
        return getCurrentEnvironment().getSecret();
    }

    @Override
    public boolean validateOAuthUrl(String url) {
        return !environmentManager.getEnvironments().isEmpty() && environmentManager.getEnvironment(url) != null;
    }

    private Environment getCurrentEnvironment() {
        Environment environment =
                environmentManager.getEnvironment(CoreLibrary.getInstance().context().allSharedPreferences().fetchBaseURL(AllConstants.DRISHTI_BASE_URL));
        if (environment != null) {
            return environment;
        } else {
            throw new IllegalStateException("No client found for the given url");
        }
    }

}
