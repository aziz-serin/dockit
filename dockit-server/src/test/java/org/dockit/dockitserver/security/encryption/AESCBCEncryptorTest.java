package org.dockit.dockitserver.security.encryption;

import org.dockit.dockitserver.security.key.KeyConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.security.InvalidKeyException;

import static org.junit.Assert.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

public class AESCBCEncryptorTest {
    private static final int DES_SIZE = 56;
    private static final String UNSUPPORTED_ALGORITHM = "DES";
    private static final String DATA = "Message to encrypt";

    private static SecretKey validKey;
    private static SecretKey invalidKey;

    @BeforeAll
    public static void setup() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(UNSUPPORTED_ALGORITHM);
        keyGenerator.init(DES_SIZE);
        invalidKey = keyGenerator.generateKey();

        keyGenerator = KeyGenerator.getInstance(KeyConstants.AES_CIPHER);
        keyGenerator.init(KeyConstants.ENCRYPTION_KEY_SIZE);
        validKey = keyGenerator.generateKey();
    }

    @Test
    public void encryptThrowsExceptionGivenNullKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESCBCEncryptor.encrypt(DATA, null);
        });
    }

    @Test
    public void encryptThrowsExceptionGivenNullData() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESCBCEncryptor.encrypt(null, validKey);
        });
    }

    @Test
    public void encryptThrowsExceptionGivenInvalidKey() {
        assertThrows(InvalidKeyException.class, () -> {
            AESCBCEncryptor.encrypt(DATA, invalidKey);
        });
    }

    @Test
    public void encryptSucceeds() throws Exception {
        AESCBCEncryptor.encrypt(DATA, validKey);
    }

    @Test
    public void decryptThrowsExceptionGivenNullKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESCBCEncryptor.decrypt(DATA, null);
        });
    }

    @Test
    public void decryptThrowsExceptionGivenNullData() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESCBCEncryptor.decrypt(null, validKey);
        });
    }

    @Test
    public void decryptThrowsExceptionGivenInvalidKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESCBCEncryptor.decrypt(DATA, invalidKey);
        });
    }

    @Test
    public void decryptThrowsExceptionGivenUnencryptedTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            AESCBCEncryptor.decrypt(DATA, validKey);
        });
    }

    @Test
    public void decryptCanReverseEncryption() throws Exception {
        String encrypted = AESCBCEncryptor.encrypt(DATA, validKey);
        String decrypted = AESCBCEncryptor.decrypt(encrypted, validKey);

        assertThat(decrypted).isEqualTo(DATA);
    }
}
