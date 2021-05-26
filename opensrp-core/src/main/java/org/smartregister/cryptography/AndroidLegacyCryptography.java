package org.smartregister.cryptography;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import org.smartregister.CoreLibrary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 26/04/2019.
 */
public class AndroidLegacyCryptography extends BaseCryptography implements ICryptography {

    public static final String AES_MODE = "AES/ECB/PKCS7Padding";

    public AndroidLegacyCryptography(Context context) {
        super(context);
    }

    @Override
    public String getAESMode() {
        return AES_MODE;
    }

    @Override
    public byte[] encrypt(byte[] input, String keyAlias) {
        try {
            Cipher c = Cipher.getInstance(AES_MODE, PROVIDER.BOUNCY_CASTLE);
            c.init(Cipher.ENCRYPT_MODE, getKey(keyAlias));
            byte[] encodedBytes = c.doFinal(input);
            return encodedBytes;

        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    @Override
    public byte[] decrypt(byte[] encrypted, String keyAlias) {
        try {
            Cipher c = Cipher.getInstance(AES_MODE, PROVIDER.BOUNCY_CASTLE);
            c.init(Cipher.DECRYPT_MODE, getKey(keyAlias));
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

            String enryptedKeyB64 = CoreLibrary.getInstance().context().allSharedPreferences().getPreference(keyAlias);

            byte[] encryptedKey = Base64.decode(enryptedKeyB64, Base64.DEFAULT);
            byte[] key = rsaDecrypt(encryptedKey, keyAlias + ALGORITHM.RSA);
            return new SecretKeySpec(key, ALGORITHM.AES);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    @Override
    public void generateKey(String keyAlias) {
        try {

            String RSA_KEY_ALIAS = keyAlias + ALGORITHM.RSA;

            generateRSAKeys(RSA_KEY_ALIAS);
            generateAESKey(keyAlias, RSA_KEY_ALIAS);

        } catch (Exception e) {
            Timber.e(e);
        }

    }

    private byte[] rsaEncrypt(byte[] secret, String rsaKeyAlias) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) getKeyStore().getEntry(rsaKeyAlias, null);
        // Encrypt the text
        Cipher inputCipher = Cipher.getInstance(RSA_MODE, AndroidLegacyCryptography.PROVIDER.ANDROID_OPEN_SSL);
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
        cipherOutputStream.write(secret);
        cipherOutputStream.close();

        return outputStream.toByteArray();
    }

    private byte[] rsaDecrypt(byte[] encrypted, String rsaKeyAlias) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) getKeyStore().getEntry(rsaKeyAlias, null);
        Cipher output = Cipher.getInstance(RSA_MODE, AndroidLegacyCryptography.PROVIDER.ANDROID_OPEN_SSL);
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(encrypted), output);
        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte) nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i).byteValue();
        }
        return bytes;
    }

    public String generateAESKey(String encryptedAESKeyKey, String rsaPrivateKeyAlias) throws Exception {
        String enryptedKeyB64 = CoreLibrary.getInstance().context().allSharedPreferences().getPreference(encryptedAESKeyKey);
        if (enryptedKeyB64 == null) {
            byte[] key = new byte[16];
            secureRandom.nextBytes(key);
            byte[] encryptedKey = rsaEncrypt(key, rsaPrivateKeyAlias);
            enryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);
            CoreLibrary.getInstance().context().allSharedPreferences().savePreference(encryptedAESKeyKey, enryptedKeyB64);
        }

        return enryptedKeyB64;
    }

    public void generateRSAKeys(String rsaKeyAlias) {
        try {
// Generate the RSA key pairs
            if (!getKeyStore().containsAlias(rsaKeyAlias)) {
                // Generate a key pair for encryption
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 50);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(rsaKeyAlias)
                        .setSubject(new X500Principal("CN=" + rsaKeyAlias + ", OU=OpenSRP, O=ONA, C=KE"))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, AndroidKeyStore);
                kpg.initialize(spec);
                kpg.generateKeyPair();
            }
        } catch (Exception e) {

            Timber.e(e);
        }
    }
}
