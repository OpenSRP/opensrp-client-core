package org.smartregister.util;

import org.smartregister.BuildConfig;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.account.AccountHelper;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.security.PasswordHash;
import org.smartregister.security.SecurityHelper;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by ndegwamartin on 22/07/2020.
 */
public class CredentialsHelper {
    private Context context;
    private AllSharedPreferences allSharedPreferences;

    public CredentialsHelper(Context context) {
        this.context = context;
        this.allSharedPreferences = context.allSharedPreferences();
    }

    public static boolean shouldMigrate() {
        return CoreLibrary.getInstance().context().allSharedPreferences().getDBEncryptionVersion() == 0 ||
                (CoreLibrary.getInstance().context().allSharedPreferences().getDBEncryptionVersion() > 0 &&
                        BuildConfig.DB_ENCRYPTION_VERSION > CoreLibrary.getInstance().context().allSharedPreferences().getDBEncryptionVersion());
    }

    public byte[] getCredentials(String username, String type) {

        if (CREDENTIALS_TYPE.DB_AUTH.equals(type)) {

            return context.userService().getDecryptedPassphraseValue(username);

        } else if (CREDENTIALS_TYPE.LOCAL_AUTH.equals(type)) {

            return context.userService().getDecryptedAccountValue(username, AccountHelper.INTENT_KEY.ACCOUNT_LOCAL_PASSWORD);

        }

        return null;
    }


    public void saveCredentials(String type, String encryptedPassphrase,String username) {

        if (CREDENTIALS_TYPE.DB_AUTH.equals(type)) {

            allSharedPreferences.savePassphrase(encryptedPassphrase, CoreLibrary.getInstance().getSyncConfiguration().getEncryptionParam().name(),username);
            allSharedPreferences.setDBEncryptionVersion(BuildConfig.DB_ENCRYPTION_VERSION);

        }/* else if (CREDENTIALS_TYPE.LOCAL_AUTH.equals(type)) {

            //saved in Account Manager by caller
        }*/

    }

    public PasswordHash generateLocalAuthCredentials(char[] password) throws InvalidKeySpecException, NoSuchAlgorithmException {

        if (password == null)
            return null;

        return SecurityHelper.getPasswordHash(password);
    }

    public byte[] generateDBCredentials(char[] password, LoginResponseData userInfo) {

        char[] encryptionParamValue = null;

        SyncConfiguration syncConfiguration = CoreLibrary.getInstance().getSyncConfiguration();
        if (syncConfiguration.getEncryptionParam() != null) {
            SyncFilter syncFilter = syncConfiguration.getEncryptionParam();
            if (SyncFilter.TEAM.equals(syncFilter) || SyncFilter.TEAM_ID.equals(syncFilter)) {
                encryptionParamValue = context.userService().getUserDefaultTeamId(userInfo).toCharArray();
            } else if (SyncFilter.LOCATION.equals(syncFilter) || SyncFilter.LOCATION_ID.equals(syncFilter)) {
                encryptionParamValue = context.userService().getUserLocationId(userInfo).toCharArray();
            } else if (SyncFilter.PROVIDER.equals(syncFilter)) {
                encryptionParamValue = password;
            }
        }

        if (encryptionParamValue == null || encryptionParamValue.length < 1) {
            return null;
        }

        return SecurityHelper.toBytes(encryptionParamValue);

    }

    public class CREDENTIALS_TYPE {
        public static final String LOCAL_AUTH = "local_auth";
        public static final String DB_AUTH = "db_auth";
    }
}
