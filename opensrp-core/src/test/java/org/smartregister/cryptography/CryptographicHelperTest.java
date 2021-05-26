package org.smartregister.cryptography;

import android.os.Build;

import org.apache.commons.lang3.CharEncoding;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.UnsupportedEncodingException;

/**
 * Created by ndegwamartin on 2019-05-22.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O_MR1)
public class CryptographicHelperTest {

    public static final String SAMPLE_STRING = "I am a high security string that needs to be hidden from prying eyes";

    public static final String SAMPLE_KEY_ALIAS = "SampleKeyAlias";

    private CryptographicHelper cryptographicHelper;

    @Mock
    private AndroidMCryptography androidMCryptography;

    @Mock
    private AndroidLegacyCryptography androidLegacyCryptography;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cryptographicHelper = CryptographicHelper.getInstance(RuntimeEnvironment.application);

        Assert.assertNotNull(cryptographicHelper);
        cryptographicHelper.setMCryptography(androidMCryptography);
        cryptographicHelper.setLegacyCryptography(androidLegacyCryptography);
    }

    @Test
    public void testCryptographicHelperInitsCorrectly() {
        Assert.assertNotNull(cryptographicHelper);
    }

    @Test
    public void testCryptographicHelperEncryptInvokesAndroidMEncryptMethod() throws UnsupportedEncodingException {
        cryptographicHelper.encrypt(SAMPLE_STRING.getBytes(CharEncoding.UTF_8), SAMPLE_KEY_ALIAS);

        Mockito.verify(androidMCryptography).encrypt(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyString());
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void testCryptographicHelperEncryptInvokesLegacyEncryptMethod() throws UnsupportedEncodingException {
        cryptographicHelper.encrypt(SAMPLE_STRING.getBytes(CharEncoding.UTF_8), SAMPLE_KEY_ALIAS);

        Mockito.verify(androidLegacyCryptography).encrypt(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyString());
    }

    @Test
    public void testCryptographicHelperDecrypteInvokesAndroidMDecryptMethod() throws UnsupportedEncodingException {
        cryptographicHelper.decrypt(SAMPLE_STRING.getBytes(CharEncoding.UTF_8), SAMPLE_KEY_ALIAS);

        Mockito.verify(androidMCryptography).decrypt(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyString());
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void testCryptographicHelperDecryptInvokesLegacyDecryptMethod() throws UnsupportedEncodingException {
        cryptographicHelper.decrypt(SAMPLE_STRING.getBytes(CharEncoding.UTF_8), SAMPLE_KEY_ALIAS);

        Mockito.verify(androidLegacyCryptography).decrypt(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyString());
    }

    @Test
    public void testCryptographicHelperGenerateKeyInvokesAndroidMGenerateKeyMethod() {
        cryptographicHelper.generateKey(SAMPLE_KEY_ALIAS);

        Mockito.verify(androidMCryptography).generateKey(SAMPLE_KEY_ALIAS);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void testCryptographicHelperGenerateKeyInvokesLegacyGenerateKeyMethod() {
        cryptographicHelper.generateKey(SAMPLE_KEY_ALIAS);

        Mockito.verify(androidLegacyCryptography).generateKey(SAMPLE_KEY_ALIAS);
    }

    @Test
    public void testCryptographicHelperGetKeyInvokesAndroidMGetKeyMethod() {
        cryptographicHelper.getKey(SAMPLE_KEY_ALIAS);

        Mockito.verify(androidMCryptography).getKey(SAMPLE_KEY_ALIAS);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void testCryptographicHelperGetKeyInvokesLegacyGetKeyMethod() {
        cryptographicHelper.getKey(SAMPLE_KEY_ALIAS);

        Mockito.verify(androidLegacyCryptography).getKey(SAMPLE_KEY_ALIAS);
    }

    @Test
    public void testCryptographicHelperDeleteKeyInvokesAndroidMGetKeyMethod() {
        cryptographicHelper.deleteKey(SAMPLE_KEY_ALIAS);

        Mockito.verify(androidMCryptography).deleteKey(SAMPLE_KEY_ALIAS);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void testCryptographicHelperDeleteKeyInvokesLegacyGetKeyMethod() {
        cryptographicHelper.deleteKey(SAMPLE_KEY_ALIAS);

        Mockito.verify(androidLegacyCryptography).deleteKey(SAMPLE_KEY_ALIAS);
    }
}
