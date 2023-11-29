package org.dockit.dockitserver.security.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

public class AESKeyGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AESKeyGenerator.class);

    protected static Optional<Key> generateKey(String algorithm) {
        try {
            return Optional.of(generateAESKey(algorithm, KeyConstants.KEY_SIZE));
        } catch (NoSuchAlgorithmException e) {
            logger.debug("Given algorithm {} is not found!", algorithm);
            return Optional.empty();
        }
    }

    protected static Optional<Key> generateKey(String algorithm, String password) {
        try {
            return Optional.of(generatePasswordBasedAESKey(algorithm, KeyConstants.KEY_SIZE, password.toCharArray()));
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

    private static Key generatePasswordBasedAESKey(String cipher, int keySize, char[] password) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        byte[] salt = new byte[100];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, 1000, keySize);
        SecretKey pbeKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(pbeKeySpec);
        return new SecretKeySpec(pbeKey.getEncoded(), cipher);
    }

}
