package org.smartregister.security;

import android.os.Build;
import android.text.Editable;
import android.util.Base64;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by ndegwamartin on 04/06/2020.
 */
public class SecurityHelper {

    private static final Charset CHARSET = Charset.forName(CharEncoding.UTF_8);
    public static final int ITERATION_COUNT = 200048;
    private static final int PASSPHRASE_SIZE = 32;

    /**
     * This method ensures that sensitive info can be collected for the edit text in a safer way
     */
    public static char[] readValue(Editable editable) {

        char[] chars = new char[editable.length()];
        editable.getChars(0, editable.length(), chars, 0);

        return chars;
    }

    /**
     * This method allows us to overwrite byte array data
     *
     * @param array character array
     */
    public static void clearArray(byte[] array) {
        if (array != null) {
            Arrays.fill(array, (byte) 0);
        }
    }

    /**
     * This method allows us to overwrite byte array data thus removing original values from memory
     *
     * @param array character array
     */
    public static void clearArray(char[] array) {
        if (array != null) {
            Arrays.fill(array, '*');
        }
    }

    /**
     * This method converts characters in the string buffer to byte array without creating a String object
     *
     * @param stringBuffer
     * @return an array of bytes , a conversion from the string buffer
     */

    public static byte[] toBytes(StringBuffer stringBuffer) throws CharacterCodingException {

        CharsetEncoder encoder = CHARSET.newEncoder();

        CharBuffer buffer = CharBuffer.wrap(stringBuffer);

        ByteBuffer bytesBuffer = encoder.encode(buffer);

        byte[] bytes = bytesBuffer.array();

        clearArray(bytesBuffer.array());

        clearStringBuffer(stringBuffer);

        return bytes;
    }

    private static void clearStringBuffer(StringBuffer stringBuffer) {
        stringBuffer.setLength(0);
        stringBuffer.append("*");
    }

    /**
     * This method converts characters in the char array buffer to a byte array
     *
     * @param chars array
     * @return an array of bytes, a conversion from the chars array
     */
    public static byte[] toBytes(char[] chars) {

        return SQLiteDatabase.getBytes(chars);

    }

    /**
     * This method converts characters in the byte array buffer to a char array
     *
     * @param bytes array
     * @return an array of chars, a conversion from the bytes array
     */
    public static char[] toChars(byte[] bytes) {

        return SQLiteDatabase.getChars(bytes);
    }

    /**
     * @param password password to hash
     * @return Password Hash object containing the bytes of the salt and the hashed password
     * @throws NoSuchAlgorithmException when the Android API level doesn't support the specified Algorithm
     * @throws InvalidKeySpecException  when the Key used for generation has an invalid configuration
     */
    public static PasswordHash getPasswordHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] salt = new byte[128];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        return new PasswordHash(salt, hashPassword(password, salt));
    }

    /**
     * @param password password to hash
     * @return byte array of the bytes of the salt and the hashed password
     */
    public static byte[] hashPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {

        int keyLength = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 256 : 160;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? "PBKDF2withHmacSHA256" : "PBKDF2WithHmacSHA1");

        KeySpec pbKeySpec = new PBEKeySpec(password, salt, ITERATION_COUNT, keyLength);

        return secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
    }

    /**
     * @param base64EncodedValue The base64 Encoded value
     * @return decoded array of bytes
     */
    public static byte[] nullSafeBase64Decode(String base64EncodedValue) {
        if (!StringUtils.isBlank(base64EncodedValue)) {
            return Base64.decode(base64EncodedValue, Base64.DEFAULT);
        } else {
            return null;
        }
    }

    /**
     * Generates random characters of the specified size
     *
     * @return a random array of alphanumeric chars
     */
    public static char[] generateRandomPassphrase() {

        return RandomStringUtils.randomAlphanumeric(PASSPHRASE_SIZE).toCharArray();
    }

}
