package org.smartregister.cryptography;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.security.Key;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 26/04/2019.
 * <p>
 * This class wraps the two version encryption classes to provide one class for interaction
 */
public class CryptographicHelper {


    private static WeakReference<Context> context;

    private static CryptographicHelper cryptographicHelper;
    private static AndroidLegacyCryptography legacyCryptography;
    private static AndroidMCryptography mCryptography;


    public static CryptographicHelper getInstance(Context context_) {

        if (cryptographicHelper == null) {
            try {
                cryptographicHelper = new CryptographicHelper();

                context = new WeakReference<>(context_);

                legacyCryptography = new AndroidLegacyCryptography(context.get());
                mCryptography = new AndroidMCryptography(context.get());

            } catch (Exception e) {
                Timber.e(e);
            }
        }

        return cryptographicHelper;
    }

    public static byte[] encrypt(byte[] data, String keyAlias) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            return legacyCryptography.encrypt(data, keyAlias);
        } else {
            return mCryptography.encrypt(data, keyAlias);
        }
    }

    public static byte[] decrypt(byte[] data, String keyAlias) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            return legacyCryptography.decrypt(data, keyAlias);
        } else {
            return mCryptography.decrypt(data, keyAlias);
        }
    }


    public void generateKey(String keyAlias) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            legacyCryptography.generateKey(keyAlias);
        } else {
            mCryptography.generateKey(keyAlias);
        }

    }

    public Key getKey(String keyAlias) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            return legacyCryptography.getKey(keyAlias);
        } else {
            return mCryptography.getKey(keyAlias);
        }
    }

    public void deleteKey(String keyAlias) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            legacyCryptography.deleteKey(keyAlias);
        } else {
            mCryptography.deleteKey(keyAlias);
        }
    }

    public void setLegacyCryptography(AndroidLegacyCryptography legacyCryptography) {
        this.legacyCryptography = legacyCryptography;
    }

    public void setMCryptography(AndroidMCryptography androidMCryptography) {
        this.mCryptography = androidMCryptography;
    }
}
