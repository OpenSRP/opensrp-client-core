package org.smartregister.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.service.HTTPAgent;
import org.smartregister.view.activity.BaseLoginActivity;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 20-04-2021.
 */
public class AccountAuthenticatorTest extends BaseRobolectricUnitTest {

    private AccountAuthenticator accountAuthenticator;

    @Before
    public void setUp() throws Exception {
        accountAuthenticator = new AccountAuthenticator(RuntimeEnvironment.application);
    }

    @Test
    public void addAccountShouldReturnBundleWithCorrectDetails() throws NetworkErrorException {
        AccountAuthenticatorResponse accountAuthenticatorResponse = Mockito.mock(AccountAuthenticatorResponse.class);
        String accountType = "org.smartregister.app";
        String authTokenType = "oauth2";
        Bundle bundle = accountAuthenticator.addAccount(accountAuthenticatorResponse, accountType, authTokenType, null, null);

        Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
        Assert.assertEquals(accountType, intent.getStringExtra(AccountHelper.INTENT_KEY.ACCOUNT_TYPE));
        Assert.assertEquals(authTokenType, intent.getStringExtra(AccountHelper.INTENT_KEY.AUTH_TYPE));
        Assert.assertTrue(intent.getBooleanExtra(AccountHelper.INTENT_KEY.IS_NEW_ACCOUNT, false));
        Assert.assertEquals(accountAuthenticatorResponse, intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));
    }

    @Test
    public void getAuthTokenLabelShouldReturnUpperCaseString() {
        Assert.assertEquals("MY-LABEL", accountAuthenticator.getAuthTokenLabel("my-label"));
    }

    @Test
    public void hasFeaturesShouldReturnBundleWithFalseBooleanResult() throws NetworkErrorException {
        Bundle result = accountAuthenticator.hasFeatures(null, null, null);

        Assert.assertFalse(result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
        Assert.assertEquals(1, result.keySet().size());
    }

    @Test
    public void editPropertiesShouldReturnNullAlways() {
        Assert.assertNull(accountAuthenticator.editProperties(null, "account-type-1"));
        Assert.assertNull(accountAuthenticator.editProperties(Mockito.mock(AccountAuthenticatorResponse.class), "account-type-1"));
        Assert.assertNull(accountAuthenticator.editProperties(Mockito.mock(AccountAuthenticatorResponse.class), null));
    }

    @Test
    public void confirmCredentialsShouldDoNothingAndReturnOptions() throws NetworkErrorException {
        Bundle bundle = Mockito.mock(Bundle.class);
        AccountAuthenticatorResponse accountAuthenticatorResponse = Mockito.mock(AccountAuthenticatorResponse.class);
        Assert.assertEquals(bundle, accountAuthenticator.confirmCredentials(accountAuthenticatorResponse, null, bundle));

        Mockito.verifyNoInteractions(bundle, accountAuthenticatorResponse);
    }

    @Test
    public void updateCredentialsShouldReturnBundleWithCorrectDetails() throws NetworkErrorException {
        AccountAuthenticatorResponse accountAuthenticatorResponse = Mockito.mock(AccountAuthenticatorResponse.class);
        String authTokenType = "oauth2";
        Bundle bundle = accountAuthenticator.updateCredentials(accountAuthenticatorResponse, null, authTokenType, null);

        Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
        Assert.assertEquals(authTokenType, intent.getStringExtra(AccountHelper.INTENT_KEY.AUTH_TYPE));
        Assert.assertFalse(intent.getBooleanExtra(AccountHelper.INTENT_KEY.IS_NEW_ACCOUNT, false));
        Assert.assertEquals(accountAuthenticatorResponse, intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));
    }

    @Test
    public void getAuthTokenShouldReturnAuthTokenFromAccountManager() throws NetworkErrorException {
        AccountManager accountManager = Mockito.mock(AccountManager.class);
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "accountManager", accountManager);

        AccountAuthenticatorResponse accountAuthenticatorResponse = Mockito.mock(AccountAuthenticatorResponse.class);
        String authToken = "my-token-kenya";
        String authTokenType = "my-token-type";
        String accountName = "Goldsmith";
        String accountType = "org.smartregister.goldsmith";
        Account account = new Account(accountName, accountType);
        Bundle bundle = new Bundle();

        Mockito.doReturn(authToken).when(accountManager).peekAuthToken(account, authTokenType);


        // Call the method under tests
        Bundle actualResult = accountAuthenticator.getAuthToken(accountAuthenticatorResponse, account, authTokenType, bundle);

        // Perform assertions
        Assert.assertEquals(accountName, actualResult.getString(AccountManager.KEY_ACCOUNT_NAME));
        Assert.assertEquals(accountType, actualResult.getString(AccountManager.KEY_ACCOUNT_TYPE));
        Assert.assertEquals(authToken, actualResult.getString(AccountManager.KEY_AUTHTOKEN));

        // Reset the accountManager in CoreLibrary
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "accountManager", null);
    }

    @Test
    public void getAuthTokenShouldTryToRefreshTokenWhenAuthTokenNotAvailable() throws NetworkErrorException {
        AccountManager accountManager = Mockito.mock(AccountManager.class);
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "accountManager", accountManager);

        AccountAuthenticatorResponse accountAuthenticatorResponse = Mockito.mock(AccountAuthenticatorResponse.class);
        String accessToken = "my-token-kenya";
        String accountManagerPassword = "my-token-kenya";
        String authTokenType = "my-token-type";
        String accountName = "Goldsmith";
        String accountType = "org.smartregister.goldsmith";
        Account account = new Account(accountName, accountType);
        Bundle bundle = new Bundle();

        Mockito.doReturn(null).when(accountManager).peekAuthToken(account, authTokenType);
        Mockito.doReturn(accountManagerPassword).when(accountManager).getPassword(account);

        // Mock HttpAgent response
        HTTPAgent originalHttpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        HTTPAgent httpAgent = Mockito.spy(originalHttpAgent);
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "httpAgent", httpAgent);

        AccountResponse accountResponse = Mockito.mock(AccountResponse.class);
        Mockito.doReturn(HttpStatus.SC_OK).when(accountResponse).getStatus();
        Mockito.doReturn(accessToken).when(accountResponse).getAccessToken();
        Mockito.doReturn(accountManagerPassword).when(accountResponse).getRefreshToken();

        Mockito.doReturn(accountResponse).when(httpAgent).oauth2authenticateRefreshToken(accountManagerPassword);


        // Call the method under tests
        Bundle actualResult = accountAuthenticator.getAuthToken(accountAuthenticatorResponse, account, authTokenType, bundle);

        // Perform assertions
        Assert.assertEquals(accountName, actualResult.getString(AccountManager.KEY_ACCOUNT_NAME));
        Assert.assertEquals(accountType, actualResult.getString(AccountManager.KEY_ACCOUNT_TYPE));
        Assert.assertEquals(accessToken, actualResult.getString(AccountManager.KEY_AUTHTOKEN));

        Mockito.verify(accountManager).setPassword(account, accountManagerPassword);
        Mockito.verify(accountManager).setAuthToken(account, authTokenType, accessToken);
        Mockito.verify(accountManager).notifyAccountAuthenticated(account);

        // Reset the accountManager & httpAgent in CoreLibrary

        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "httpAgent", originalHttpAgent);
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "accountManager", null);
    }

    @Test
    public void getAuthTokenShouldTryToRefreshTokenAndRequestReauthenticationViaIntentWhenAuthTokenNotAvailableAndRefreshTokenFails() throws NetworkErrorException {
        AccountManager accountManager = Mockito.mock(AccountManager.class);
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "accountManager", accountManager);

        AccountAuthenticatorResponse accountAuthenticatorResponse = Mockito.mock(AccountAuthenticatorResponse.class);
        String accountManagerPassword = "my-token-kenya";
        String authTokenType = "my-token-type";
        String accountName = "Goldsmith";
        String accountType = "org.smartregister.goldsmith";
        Account account = new Account(accountName, accountType);
        Bundle bundle = new Bundle();

        Mockito.doReturn(null).when(accountManager).peekAuthToken(account, authTokenType);
        Mockito.doReturn(accountManagerPassword).when(accountManager).getPassword(account);

        // Mock HttpAgent response
        HTTPAgent originalHttpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        HTTPAgent httpAgent = Mockito.spy(originalHttpAgent);
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "httpAgent", httpAgent);

        AccountResponse accountResponse = Mockito.mock(AccountResponse.class);
        Mockito.doReturn(HttpStatus.SC_UNAUTHORIZED).when(accountResponse).getStatus();
        Mockito.doReturn(accountResponse).when(httpAgent).oauth2authenticateRefreshToken(accountManagerPassword);


        // Call the method under tests
        Bundle actualResult = accountAuthenticator.getAuthToken(accountAuthenticatorResponse, account, authTokenType, bundle);

        // Perform assertions
        Intent reauthenticationIntent = actualResult.getParcelable(AccountManager.KEY_INTENT);

        Assert.assertEquals(accountType, reauthenticationIntent.getStringExtra(AccountHelper.INTENT_KEY.ACCOUNT_TYPE));
        Assert.assertEquals(authTokenType, reauthenticationIntent.getStringExtra(AccountHelper.INTENT_KEY.AUTH_TYPE));
        Assert.assertEquals(accountAuthenticatorResponse, reauthenticationIntent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));
        Assert.assertEquals(BaseLoginActivity.class.getName(), reauthenticationIntent.getComponent().getClassName());

        // Reset the accountManager & httpAgent in CoreLibrary

        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "httpAgent", originalHttpAgent);
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "accountManager", null);
    }
}