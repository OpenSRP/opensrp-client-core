package org.smartregister.cryptography;

import android.os.Build;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.security.KeyStore;
import java.security.KeyStoreException;

/**
 * Created by ndegwamartin on 2019-05-22.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O_MR1)
public class AndroidLegacyCryptographyTest {

    @Mock
    private KeyStore keystore;

    private AndroidLegacyCryptography androidLegacyCryptography;

    @Before
    public void setUp() throws KeyStoreException {

        MockitoAnnotations.initMocks(this);
        androidLegacyCryptography = new AndroidLegacyCryptography(RuntimeEnvironment.application);
        androidLegacyCryptography.setKeyStore(keystore);
    }


    @Test
    public void testAndroidLegacyCryptographyClassInitsCorrectly() {

        Assert.assertNotNull(androidLegacyCryptography);
        Assert.assertNotNull(androidLegacyCryptography.secureRandom);
        Assert.assertNotNull(androidLegacyCryptography.getAESMode());
        Assert.assertEquals(AndroidLegacyCryptography.AES_MODE, androidLegacyCryptography.getAESMode());

    }

}
