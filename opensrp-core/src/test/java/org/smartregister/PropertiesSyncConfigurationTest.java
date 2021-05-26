package org.smartregister;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.Environment;

import java.util.ArrayList;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 20-04-2021.
 */
public class PropertiesSyncConfigurationTest extends BaseRobolectricUnitTest {

    @Mock
    private Environment environment;

    @Mock
    private EnvironmentManager environmentManager;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private PropertiesSyncConfiguration propertiesSyncConfiguration;

    @Before
    public void setUp() throws Exception {
        Mockito.doReturn(environment).when(environmentManager).getEnvironment(Mockito.nullable(String.class));
        ReflectionHelpers.setField(propertiesSyncConfiguration, "environmentManager", environmentManager);
    }

    @Test
    public void getOauthClientId() {
        String clientId = "opensrp-client-id";
        Mockito.doReturn(clientId).when(environment).getId();

        Assert.assertEquals(clientId, propertiesSyncConfiguration.getOauthClientId());
    }

    @Test
    public void getOauthClientSecret() {
        String clientSecret = "opensrp-client-secret";
        Mockito.doReturn(clientSecret).when(environment).getSecret();

        Assert.assertEquals(clientSecret, propertiesSyncConfiguration.getOauthClientSecret());
    }


    @Test(expected = IllegalStateException.class)
    public void getOauthClientSecretShouldThrowExceptionWhen() {
        Mockito.doReturn(null).when(environmentManager).getEnvironment(Mockito.nullable(String.class));

        propertiesSyncConfiguration.getOauthClientSecret();
    }

    @Test
    public void validateOAuthUrlShouldReturnTrueWhenEnvironmentExists() {
        String url = "https://goldsmith-stage.smartregister.org/opensrp";
        ArrayList<Environment> environments = new ArrayList<>();
        environments.add(environment);
        Mockito.doReturn(environment).when(environmentManager).getEnvironment(url);
        Mockito.doReturn(environments).when(environmentManager).getEnvironments();

        Assert.assertTrue(propertiesSyncConfiguration.validateOAuthUrl(url));
    }

    @Test
    public void validateOAuthUrlShouldReturnFalseWhenEnvironmentsAreEmpty() {
        String url = "https://goldsmith-stage.smartregister.org/opensrp";

        Assert.assertFalse(propertiesSyncConfiguration.validateOAuthUrl(url));
    }

    @Test
    public void validateOAuthUrlShouldReturnFalseWhenEnvironmentDoesNotExist() {
        String url = "https://opensrp-goldsmith-stage.org/";
        ArrayList<Environment> environments = new ArrayList<>();
        environments.add(environment);
        Mockito.doReturn(environments).when(environmentManager).getEnvironments();
        Mockito.doReturn(null).when(environmentManager).getEnvironment(url);

        Assert.assertFalse(propertiesSyncConfiguration.validateOAuthUrl(url));
    }

}