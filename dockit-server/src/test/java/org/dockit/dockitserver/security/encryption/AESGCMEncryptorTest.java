package org.dockit.dockitserver.security.encryption;

import org.dockit.dockitserver.security.key.AESKeyGenerator;
import org.dockit.dockitserver.security.key.KeyConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.AEADBadTagException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class AESGCMEncryptorTest {
    private static final int DES_SIZE = 56;
    private static final String UNSUPPORTED_ALGORITHM = "DES";
    private static final String DATA = "Message to encrypt";
    private static final String AUTHENTICATION_TAG = "tag";
    private static final String WRONG_AUTHENTICATION_TAG = "wrongtag";

    private static SecretKey validKey;
    private static SecretKey invalidKey;

    @BeforeAll
    public static void setup() throws Exception {
        validKey = (SecretKey) AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER).get();

        KeyGenerator keyGenerator = KeyGenerator.getInstance(UNSUPPORTED_ALGORITHM);
        keyGenerator.init(DES_SIZE);
        invalidKey = keyGenerator.generateKey();
    }

    @Test
    public void encryptThrowsExceptionGivenNullKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESGCMEncryptor.encrypt(DATA, AUTHENTICATION_TAG, null);
        });
    }

    @Test
    public void encryptThrowsExceptionGivenNullData() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESGCMEncryptor.encrypt(null, AUTHENTICATION_TAG, validKey);
        });
    }

    @Test
    public void encryptThrowsExceptionGivenNullAuthenticationTag() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESGCMEncryptor.encrypt(DATA, null, validKey);
        });
    }

    @Test
    public void encryptThrowsExceptionGivenInvalidKey() {
        assertThrows(InvalidKeyException.class, () -> {
            AESGCMEncryptor.encrypt(DATA, AUTHENTICATION_TAG, invalidKey);
        });
    }

    @Test
    public void encryptSucceeds() throws Exception {
        AESGCMEncryptor.encrypt(DATA, AUTHENTICATION_TAG, validKey);
    }

    @Test
    public void decryptThrowsExceptionGivenNullKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESGCMEncryptor.decrypt(DATA,  AUTHENTICATION_TAG, null);
        });
    }

    @Test
    public void decryptThrowsExceptionGivenNullData() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESGCMEncryptor.decrypt(null, AUTHENTICATION_TAG, validKey);
        });
    }

    @Test
    public void decryptThrowsExceptionGivenNullAuthenticationTag() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESGCMEncryptor.decrypt(DATA, AUTHENTICATION_TAG, validKey);
        });
    }

    @Test
    public void decryptThrowsExceptionGivenInvalidKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESGCMEncryptor.decrypt(DATA, AUTHENTICATION_TAG, invalidKey);
        });
    }

    @Test
    public void decryptFailsGivenInvalidAAD() throws Exception{
        String encrypted = AESGCMEncryptor.encrypt(DATA, AUTHENTICATION_TAG, validKey);
        assertThrows(AEADBadTagException.class, () -> {
            AESGCMEncryptor.decrypt(encrypted, WRONG_AUTHENTICATION_TAG, validKey);
        });
    }

    @Test
    public void decryptCanReverseEncryption() throws Exception {
        String encrypted = AESGCMEncryptor.encrypt(DATA, AUTHENTICATION_TAG, validKey);
        String decrypted = AESGCMEncryptor.decrypt(encrypted, AUTHENTICATION_TAG, validKey);

        assertThat(decrypted).isEqualTo(DATA);
    }
}
