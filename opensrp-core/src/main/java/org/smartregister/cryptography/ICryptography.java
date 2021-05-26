package org.smartregister.cryptography;

import java.security.Key;

/**
 * Created by ndegwamartin on 26/04/2019.
 */
public interface ICryptography {

    byte[] encrypt(byte[] input, String keyAlias);

    byte[] decrypt(byte[] encrypted, String keyAlias);

    Key getKey(String keyAlias);

    void generateKey(String keyAlias);
}
