package org.smartregister.account;

import android.accounts.Account;
import android.accounts.AccountManager;

import org.smartregister.CoreLibrary;

/**
 * Created by ndegwamartin on 2020-04-27.
 */
public class AccountHelper {

    private static AccountManager accountManager = CoreLibrary.getInstance().getAccountManager();

    public final static String KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN";
    public final static int MAX_AUTH_RETRIES = 1;

    public static final class OAUTH {
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
        public static final String ERROR_MESSAGE = "ERROR_MESSAGE";
        public final static String ACCOUNT_PASSWORD = "ACCOUNT_PASSWORD";
        public final static String IS_NEW_ACCOUNT = "IS_NEW_ACCOUNT";
        public final static String ACCOUNT_GROUP_ID = "ACCOUNT_GROUP_ID";
    }

    public static final class TOKEN_TYPE {
        public final static String PROVIDER = "provider";
        public final static String ADMIN = "admin";
    }


    public static Account getOauthAccountByType(String accountType) {
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length > 0) {
            Account account = accounts[0];
            return account;
        }
        return null;
    }

    public static String getAccountManagerValue(String key, String accountType) {
        Account account = AccountHelper.getOauthAccountByType(accountType);
        if (account != null) {
            return CoreLibrary.getInstance().getAccountManager().getUserData(account, key);
        }
        return null;
    }

    /**
     * @param accountType   unique name to identify our account type in the Account Manager
     * @param authTokenType type of token requested from server e.g. PROVIDER, ADMIN
     * @return
     */
    public static String getOAuthToken(String accountType, String authTokenType) {
        Account account = getOauthAccountByType(accountType);

        return accountManager.peekAuthToken(account, authTokenType);
    }


}
