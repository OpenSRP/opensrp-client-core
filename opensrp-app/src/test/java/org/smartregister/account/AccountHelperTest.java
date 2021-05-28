package org.smartregister.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;

import java.io.IOException;

/**
 * Created by ndegwamartin on 26/05/2020.
 */

public class AccountHelperTest extends BaseRobolectricUnitTest {
    private static final String CORE_ACCOUNT_NAME = "demo";
    private static final String CORE_ACCOUNT_TYPE = "org.smartregister.core";
    private static final String TEST_KEY = "testKey";
    private static final String TEST_VALUE = "random-test-value";
    private static final String AUTH_TOKEN_TYPE = "My Admin Token Type";
    private static final String TEST_TOKEN_VALUE = "sample-token";

    @Mock
    private AccountManager accountManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Account[] accounts = {new Account(CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE)};
        Mockito.doReturn(accounts).when(accountManager).getAccountsByType(CORE_ACCOUNT_TYPE);

        ReflectionHelpers.setStaticField(AccountHelper.class, "accountManager", accountManager);
    }

    @Test
    public void testGetOauthAccountByType() {

        Account account = AccountHelper.getOauthAccountByNameAndType(CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE);
        Assert.assertNotNull(account);
        Assert.assertEquals(CORE_ACCOUNT_NAME, account.name);
    }

    @Test
    public void testGetAccountManagerValue() {

        Mockito.doReturn(TEST_VALUE).when(accountManager).getUserData(ArgumentMatchers.any(Account.class), ArgumentMatchers.eq(TEST_KEY));

        String value = AccountHelper.getAccountManagerValue(TEST_KEY, CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE);
        Assert.assertNotNull(value);
        Assert.assertEquals(TEST_VALUE, value);
    }

    @Test
    public void testGetOAuthToken() throws AuthenticatorException, OperationCanceledException, IOException {

        Mockito.doReturn(TEST_TOKEN_VALUE).when(accountManager).blockingGetAuthToken(new Account(CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE), AUTH_TOKEN_TYPE, true);
        String myToken = AccountHelper.getOAuthToken(CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE, AUTH_TOKEN_TYPE);
        Assert.assertNotNull(myToken);
        Assert.assertEquals(TEST_TOKEN_VALUE, myToken);
    }

    @Test
    public void testInvalidateAuthToken() {

        AccountHelper.invalidateAuthToken(CORE_ACCOUNT_TYPE, TEST_TOKEN_VALUE);
        Mockito.verify(accountManager).invalidateAuthToken(CORE_ACCOUNT_TYPE, TEST_TOKEN_VALUE);
    }

    @Test
    public void testGetCachedOAuthToken() {

        Account account = new Account(CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE);
        Mockito.doReturn(TEST_TOKEN_VALUE).when(accountManager).peekAuthToken(account, AUTH_TOKEN_TYPE);

        String cachedAuthToken = AccountHelper.getCachedOAuthToken(CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE, AUTH_TOKEN_TYPE);

        Mockito.verify(accountManager).peekAuthToken(account, AUTH_TOKEN_TYPE);
        Assert.assertEquals(TEST_TOKEN_VALUE, cachedAuthToken);
    }

    @Test
    public void testReAuthenticateUserAfterSessionExpired() {

        Account account = new Account(CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE);
        Mockito.doReturn(Mockito.mock(AccountManagerFuture.class)).when(accountManager).updateCredentials(account, AUTH_TOKEN_TYPE, null, null, null, null);

        AccountManagerFuture<Bundle> reAuthenticationFuture = AccountHelper.reAuthenticateUserAfterSessionExpired(CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE, AUTH_TOKEN_TYPE);
        Assert.assertNotNull(reAuthenticationFuture);

        Mockito.verify(accountManager).updateCredentials(account, AUTH_TOKEN_TYPE, null, null, null, null);
    }

}
