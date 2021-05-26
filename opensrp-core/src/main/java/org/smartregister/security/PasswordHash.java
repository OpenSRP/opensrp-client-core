package org.smartregister.security;

/**
 * Created by ndegwamartin on 13/06/2020.
 */
public class PasswordHash {

    private byte[] salt;
    private byte[] password;

    PasswordHash(byte[] salt, byte[] password) {
        this.salt = salt;
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getPassword() {
        return password;
    }
}
