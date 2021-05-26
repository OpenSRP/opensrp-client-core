package org.smartregister.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;

import org.smartregister.CoreLibrary;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 2020-04-27.
 */
public class AccountHelper {

    private static AccountManager accountManager = CoreLibrary.getInstance().getAccountManager();

    public final static int MAX_AUTH_RETRIES = 1;


    public static final class CONFIGURATION_CONSTANTS {

        public static final String IS_KEYCLOAK_CONFIGURED = "is_keycloack_configured";
        public final static String TOKEN_ENDPOINT_URL = "token_endpoint_url";
        public final static String AUTHORIZATION_ENDPOINT_URL = "authorization_endpoint_url";
        public final static String ISSUER_ENDPOINT_URL = "issuer_endpoint_url";
        public static final String USERINFO_ENDPOINT_URL = "userinfo_endpoint_url";
    }

    public static final class OAUTH {

        public final static String ACCOUNT_CONFIGURATION_ENDPOINT = "/rest/config/keycloak";
        public final static String TOKEN_ENDPOINT = "/oauth/token";

        public static final class GRANT_TYPE {
            public final static String PASSWORD = "password";
            public final static String REFRESH_TOKEN = "refresh_token";

        }
    }

    public static final class INTENT_KEY {

        public final static String ACCOUNT_TYPE = "ACCOUNT_TYPE";
        public final static String AUTH_TYPE = "AUTH_TYPE";
        public final static String ACCOUNT_NAME = "ACCOUNT_NAME";
        public final static String IS_NEW_ACCOUNT = "IS_NEW_ACCOUNT";
        public final static String ACCOUNT_REFRESH_TOKEN = "ACCOUNT_REFRESH_TOKEN";
        public final static String ACCOUNT_LOCAL_PASSWORD_SALT = "ACCOUNT_LOCAL_PASSWORD_SALT";
        public final static String ACCOUNT_LOCAL_PASSWORD = "ACCOUNT_LOCAL_PASSWORD";
        public final static String ACCOUNT_ROLES = "ACCOUNT_ROLES";
    }

    public static final class TOKEN_TYPE {
        public final static String PROVIDER = "provider";
        public final static String ADMIN = "admin";
    }

    /**
     * Gets OAuth Account by the account name and account type
     *
     * @param accountName name of account within account manage
     * @param accountType unique name to identify our account type in the Account Manager
     * @return Account retrieved
     */
    public static Account getOauthAccountByNameAndType(String accountName, String accountType) {

        Account[] accounts = accountManager.getAccountsByType(accountType);
        return accounts.length > 0 ? selectAccount(accounts, accountName) : null;
    }

    /**
     * Get specified Account by name from the list returned by the account manager
     *
     * @param accountName name of account within account manage
     * @param accounts    list of Accounts returned by the Account Manager
     * @return Account selected from list
     */
    public static Account selectAccount(Account[] accounts, String accountName) {
        for (Account account : accounts) {
            if (accountName.equals(account.name)) {
                return account;
            }
        }

        return null;
    }

    /**
     * Gets user data value with the specified key from the Account Manager
     *
     * @param key         of the user data value we want to retrieve
     * @param accountType unique name to identify our account type in the Account Manager
     * @param accountName name of account within account manage
     * @return access token
     */
    public static String getAccountManagerValue(String key, String accountName, String accountType) {
        Account account = AccountHelper.getOauthAccountByNameAndType(accountName, accountType);
        if (account != null) {
            return accountManager.getUserData(account, key);
        }
        return null;
    }

    /**
     * Gets OAuth Token
     *
     * @param accountName   name of account within account manage
     * @param accountType   unique name to identify our account type in the Account Manager
     * @param authTokenType type of token requested from server e.g. PROVIDER, ADMIN
     * @return access token
     */
    public static String getOAuthToken(String accountName, String accountType, String authTokenType) {
        Account account = getOauthAccountByNameAndType(accountName, accountType);

        try {
            return accountManager.blockingGetAuthToken(account, authTokenType, true);
        } catch (Exception ex) {
            Timber.e(ex, "EXCEPTION: %s", ex.toString());
            return null;
        }
    }

    /**
     * This method invalidates the auth token so that the Authenticator can fetch a new one from server
     *
     * @param accountType unique name to identify our account type in the Account Manager
     * @param authToken   token to invalidate
     */
    public static void invalidateAuthToken(String accountType, String authToken) {
        if (authToken != null)
            accountManager.invalidateAuthToken(accountType, authToken);
    }


    /**
     * @param accountName   name of account within account manage
     * @param accountType   unique name to identify our account type in the Account Manager
     * @param authTokenType type of token requested from server e.g. PROVIDER, ADMIN
     * @return access token cached
     */
    public static String getCachedOAuthToken(String accountName, String accountType, String authTokenType) {
        Account account = getOauthAccountByNameAndType(accountName, accountType);
        return account != null ? accountManager.peekAuthToken(account, authTokenType) : null;
    }

    /**
     * Prompt the user to re-authenticate
     *
     * @param accountName   name of account within account manage
     * @param accountType   unique name to identify our account type in the Account Manager
     * @param authTokenType type of token requested from server e.g. PROVIDER, ADMIN
     * @return access token
     */
    public static AccountManagerFuture<Bundle> reAuthenticateUserAfterSessionExpired(String accountName, String accountType, String authTokenType) {
        Account account = getOauthAccountByNameAndType(accountName, accountType);
        return accountManager.updateCredentials(account, authTokenType, null, null, null, null);


    }

}
