package org.smartregister.cryptography;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;

/**
 * Created by ndegwamartin on 2019-05-22.
 */

public class AndroidMCryptographyTest extends BaseUnitTest {


    private AndroidMCryptography androidMCryptography;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        androidMCryptography = new AndroidMCryptography(RuntimeEnvironment.application);
    }


    @Test
    public void testAndroidMCryptographyClassInitsCorrectly() {

        Assert.assertNotNull(androidMCryptography);
        Assert.assertNotNull(androidMCryptography.secureRandom);
        Assert.assertNotNull(androidMCryptography.getAESMode());
        Assert.assertEquals(AndroidMCryptography.AES_MODE, androidMCryptography.getAESMode());

    }

}
