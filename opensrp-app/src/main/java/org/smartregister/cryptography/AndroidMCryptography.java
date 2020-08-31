package org.smartregister.cryptography;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import androidx.annotation.RequiresApi;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 26/04/2019.
 */
public class AndroidMCryptography extends BaseCryptography implements ICryptography {

    public static final String AES_MODE = "AES/GCM/NoPadding";

    private static byte[] INITIALIZATION_VECTOR;

    private static final int IV_LENGTH_BYTES = 12;//16 bytes not working, recommended cipher.blockSize()

    public AndroidMCryptography(Context context) {
        super(context);
        generateInitializationVector();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public byte[] encrypt(byte[] input, String keyAlias) {
        try {
            Cipher c = Cipher.getInstance(AES_MODE);
            c.init(Cipher.ENCRYPT_MODE, getKey(keyAlias), new GCMParameterSpec(128, INITIALIZATION_VECTOR));
            byte[] encodedBytes = c.doFinal(input);
            return encodedBytes;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    @Override
    public String getAESMode() {
        return AES_MODE;
    }

    public void generateInitializationVector() {
        try {

            byte[] iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            INITIALIZATION_VECTOR = ivParams.getIV();

        } catch (Exception e) {
            Timber.e(e);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public byte[] decrypt(byte[] encrypted, String keyAlias) {
        try {

            Cipher c = Cipher.getInstance(AES_MODE);
            c.init(Cipher.DECRYPT_MODE, getKey(keyAlias), new GCMParameterSpec(128, INITIALIZATION_VECTOR));
            byte[] decodedBytes = c.doFinal(encrypted);
            return decodedBytes;
        } catch (Exception e) {

            Timber.e(e);
            return null;
        }
    }

    @Override
    public Key getKey(String keyAlias) {
        try {
            return getKeyStore().getKey(keyAlias, null);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void generateKey(String keyAlias) {
        {
            try {

                if (!getKeyStore().containsAlias(keyAlias)) {
                    KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
                    keyGenerator.init(
                            new KeyGenParameterSpec.Builder(keyAlias,
                                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                    .setRandomizedEncryptionRequired(false)
                                    .build());
                    keyGenerator.generateKey();
                }


            } catch (Exception e) {

                Timber.e(e);
            }
        }
    }


}
