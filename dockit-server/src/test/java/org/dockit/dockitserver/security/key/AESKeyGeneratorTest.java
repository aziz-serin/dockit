package org.dockit.dockitserver.security.key;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AESKeyGeneratorTest {

    private static final String PASSWORD = "password";

    @Test
    public void generateKeyFailsGivenInvalidAlgorithm() {
        assertTrue(AESKeyGenerator.generateKey("ASE", KeyConstants.ENCRYPTION_KEY_SIZE).isEmpty());
    }

    @Test
    public void generateKeyReturnsValidKey() {
        Optional<Key> key = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.ENCRYPTION_KEY_SIZE);

        assertTrue(key.isPresent());

        SecretKey secretKey = (SecretKey) key.get();

        assertThat(secretKey.getAlgorithm()).isEqualTo(KeyConstants.AES_CIPHER);
        // length will be in bytes, not bits
        assertThat(secretKey.getEncoded().length).isEqualTo(KeyConstants.ENCRYPTION_KEY_SIZE /8);
    }

    @Test
    public void generateKeyWithPasswordFailsGivenInvalidAlgorithm() {
        assertTrue(AESKeyGenerator.generateKey("ASE", KeyConstants.ENCRYPTION_KEY_SIZE, PASSWORD).isEmpty());
    }

    @Test
    public void generateKeyWithPasswordFailsGivenNullPassword() {
        assertTrue(AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.ENCRYPTION_KEY_SIZE, null).isEmpty());
    }

    @Test
    public void generateKeyWithPasswordReturnsValidKey() {
        Optional<Key> key = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.ENCRYPTION_KEY_SIZE, PASSWORD);

        assertTrue(key.isPresent());

        SecretKey secretKey = (SecretKey) key.get();

        assertThat(secretKey.getAlgorithm()).isEqualTo(KeyConstants.AES_CIPHER);
        // length will be in bytes, not bits
        assertThat(secretKey.getEncoded().length).isEqualTo(KeyConstants.ENCRYPTION_KEY_SIZE /8);
    }
}
