package org.smartregister.security;

import android.text.Editable;
import android.util.Base64;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


/**
 * Created by ndegwamartin on 15/06/2020.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Base64.class, SecretKeyFactory.class})
public class SecurityHelperTest {

    @Mock
    private Editable editable;

    @Mock
    private SecretKey secretKey;

    private char[] TEST_PASSWORD;

    private static final String TEST_DATA = "Some Random Test Data";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        TEST_PASSWORD = "TEST_PASSWORD".toCharArray();
    }

    @Test
    public void testReadValueClearsEditableAfterReadingValue() {

        Mockito.doReturn(2).when(editable).length();

        SecurityHelper.readValue(editable);

        ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<char[]> charsCaptor = ArgumentCaptor.forClass(char[].class);
        ArgumentCaptor<Integer> firstArgCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> lastArgCaptor = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(editable).getChars(firstArgCaptor.capture(), lengthCaptor.capture(), charsCaptor.capture(), lastArgCaptor.capture());

        Assert.assertEquals(2, lengthCaptor.getValue().intValue());
        Assert.assertEquals(0, firstArgCaptor.getValue().intValue());
        Assert.assertEquals(0, lastArgCaptor.getValue().intValue());

    }

    @Test
    public void clearArray() {
        byte[] sensitiveDataArray = SecurityHelper.toBytes(TEST_PASSWORD);
        SecurityHelper.clearArray(sensitiveDataArray);

        Assert.assertNotNull(sensitiveDataArray);

        for (byte c : sensitiveDataArray) {
            Assert.assertEquals((byte) 0, c);

        }
    }

    @Test
    public void testClearArrayOverwritesCharArrayValuesWithAsterisk() {
        char[] sensitiveDataArray = TEST_PASSWORD;
        SecurityHelper.clearArray(sensitiveDataArray);

        Assert.assertNotNull(sensitiveDataArray);

        for (char c : sensitiveDataArray) {
            Assert.assertEquals('*', c);

        }
    }

    @Test
    public void testToBytes() throws CharacterCodingException {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(TEST_PASSWORD);

        byte[] testPasswordBytes = SecurityHelper.toBytes(stringBuffer);
        Assert.assertNotNull(testPasswordBytes);
        Assert.assertEquals(TEST_PASSWORD.length + 1, testPasswordBytes.length);
    }

    @Test
    public void testToCharsConvertsByteArrayToCorrectCharArray() {

        byte[] testPasswordBytes = SecurityHelper.toBytes(TEST_PASSWORD);
        Assert.assertNotNull(testPasswordBytes);

        char[] testPasswordChars = SecurityHelper.toChars(testPasswordBytes);
        Assert.assertNotNull(testPasswordChars);
        Assert.assertTrue(Arrays.equals(TEST_PASSWORD, testPasswordChars));
    }

    @Test
    public void nullSafeBase64DecodeDecodesValidBase64EncodedCorrectly() throws UnsupportedEncodingException {

        String base64EncodedString = "U29tZSBSYW5kb20gVGVzdCBEYXRh";

        PowerMockito.mockStatic(Base64.class);
        PowerMockito.when(Base64.decode(base64EncodedString, Base64.DEFAULT)).thenReturn(TEST_DATA.getBytes(CharEncoding.UTF_8));

        byte[] decoded = SecurityHelper.nullSafeBase64Decode(base64EncodedString);
        Assert.assertNotNull(decoded);
        Assert.assertTrue(Arrays.equals(SecurityHelper.toBytes(TEST_DATA.toCharArray()), decoded));

    }

    @Test
    public void nullSafeBase64DecodeDoesNotThrowExceptionIfParameterIsNull() {
        PowerMockito.mockStatic(Base64.class);
        PowerMockito.when(Base64.decode(ArgumentMatchers.anyString(), ArgumentMatchers.eq(Base64.DEFAULT))).thenReturn(new byte[]{0, 1});

        byte[] decoded = SecurityHelper.nullSafeBase64Decode(null);
        Assert.assertNull(decoded);

    }

    @Test
    public void testGetPsswordHashReturnsHashedPasswordObject() throws Exception {

        PowerMockito.mockStatic(SecretKeyFactory.class);
        SecretKeyFactory keyFactory = PowerMockito.mock(SecretKeyFactory.class);
        PowerMockito.when(SecretKeyFactory.getInstance(ArgumentMatchers.anyString())).thenReturn(keyFactory);
        PowerMockito.when(keyFactory.generateSecret(ArgumentMatchers.any(PBEKeySpec.class))).thenReturn(secretKey);
        Mockito.doReturn(SecurityHelper.toBytes(TEST_PASSWORD)).when(secretKey).getEncoded();

        PasswordHash passwordHash = SecurityHelper.getPasswordHash(TEST_PASSWORD);

        Assert.assertNotNull(passwordHash);
        Assert.assertNotNull(passwordHash.getPassword());
        Assert.assertNotNull(passwordHash.getSalt());
    }

    @Test
    public void testGenerateRandomPassphraseGeneratesAlphanumericArray() {
        char[] value = SecurityHelper.generateRandomPassphrase();
        Assert.assertNotNull(value);
        Assert.assertTrue(StringUtils.isAlphanumeric(new StringBuilder().append(value).toString()));
        Assert.assertEquals(32, value.length);
    }
}
