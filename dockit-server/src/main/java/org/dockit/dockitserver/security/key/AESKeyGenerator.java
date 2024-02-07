package org.dockit.dockitserver.security.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;
import java.util.Optional;

/**
 * Utility class to generate AES keys for encryption/decryption
 */
public class AESKeyGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AESKeyGenerator.class);

    private static final List<Integer> keySizes = List.of(128, 192, 256);

    /**
     * Generates a normal AES key by validating the given algorithm and key size
     *
     * @param algorithm to be used when generating the key
     * @param keySize keysize to be used
     * @return {@link Optional} empty if there is an error, or the generated {@link Key}
     */
    public static Optional<Key> generateKey(String algorithm, int keySize) {
        if (!keySizes.contains(keySize)) {
            logger.debug("Given key length {} is invalid for AES!", keySize);
        }
        try {
            return Optional.of(generateAESKey(algorithm, keySize));
        } catch (NoSuchAlgorithmException e) {
            logger.debug("Given algorithm {} is not found!", algorithm);
            return Optional.empty();
        }
    }

    /**
     * Generates a password-based AES key by validating the given algorithm and key size
     *
     * @param algorithm to be used when generating the key
     * @param keySize keysize to be used
     * @param password password to be used when creating the key
     * @return {@link Optional} empty if there is an error, or the generated {@link Key}
     */
    public static Optional<Key> generateKey(String algorithm, int keySize, String password) {
        if (password == null) {
            logger.debug("Password cannot be null when creating key!");
            return Optional.empty();
        }
        if (!keySizes.contains(keySize)) {
            logger.debug("Given key length {} is invalid for AES!", keySize);
        }
        try {
            return Optional.of(generatePasswordBasedAESKey(algorithm, keySize, password.toCharArray()));
        } catch (NoSuchAlgorithmException e) {
            logger.debug("Given algorithm {} is not found!", algorithm);
            return Optional.empty();
        } catch (InvalidKeySpecException e) {
            logger.debug(e.getMessage());
            return Optional.empty();
        }
    }

    private static Key generateAESKey(String algorithm, int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

    private static Key generatePasswordBasedAESKey(String algorithm, int keySize, char[] password) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        if (!algorithm.contains("AES")) {
            throw new NoSuchAlgorithmException();
        }
        byte[] salt = new byte[128];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 65536, keySize);
        return new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), algorithm);
    }

}
