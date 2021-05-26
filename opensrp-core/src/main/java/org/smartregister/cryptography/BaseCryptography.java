package org.smartregister.cryptography;

import android.content.Context;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 26/04/2019.
 */
public abstract class BaseCryptography {

    public static final String AndroidKeyStore = "AndroidKeyStore";

    public static final String RSA_MODE = "RSA/ECB/PKCS1Padding";

    private KeyStore keyStore;

    protected Context context;

    protected SecureRandom secureRandom;

    public static class PROVIDER {
        public static final String ANDROID_OPEN_SSL = "AndroidOpenSSL";
        public static final String BOUNCY_CASTLE = "BC";
    }

    public static class ALGORITHM {
        public static final String AES = "AES";
        public static final String RSA = "RSA";
    }

    public BaseCryptography(Context context) {

        try {
            this.context = context;

            keyStore = getKeyStore();
            keyStore.load(null);

        } catch (Exception e) {
            Timber.e(e);
        }

        secureRandom = new SecureRandom();
    }

    public void deleteKey(final String alias) {
        try {
            keyStore.deleteEntry(alias);
        } catch (KeyStoreException e) {
            Timber.e(e);
        }
    }

    public KeyStore getKeyStore() throws KeyStoreException {

        if (keyStore == null) {
            keyStore = KeyStore.getInstance(AndroidKeyStore);
        }

        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {

        this.keyStore = keyStore;

    }

    public abstract String getAESMode();
}
