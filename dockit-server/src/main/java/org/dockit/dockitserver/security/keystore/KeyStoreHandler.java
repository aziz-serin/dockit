package org.dockit.dockitserver.security.keystore;

import org.dockit.dockitserver.config.ConfigContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Optional;

/**
 * Utility class to handle key store operations
 */
@Component
public class KeyStoreHandler {

    private static final Logger logger = LoggerFactory.getLogger(KeyStoreHandler.class);
    private final ConfigContainer configContainer;


    /**
     * @param configContainer {@link ConfigContainer} object to be injected
     */
    @Autowired
    public KeyStoreHandler(ConfigContainer configContainer) {
        this.configContainer = configContainer;
    }

    /**
     * Save a given key in the keystore
     *
     * @param alias alias with to store the key
     * @param secretKey {@link SecretKey} to be stored
     * @param pwdArray password for the key to be stored
     * @return true if successfully saved, false if an error occurs
     */
    public boolean saveKey(String alias, SecretKey secretKey, char[] pwdArray) {
        KeyStore.SecretKeyEntry secret
                = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter password
                = new KeyStore.PasswordProtection(pwdArray);
        KeyStore keyStore = configContainer.getKeyStore();
        try {
            keyStore.setEntry(alias, secret, password);
            return true;
        } catch (KeyStoreException e) {
            logger.debug("Possibly invalid password, check the exception: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get key from the keystore
     *
     * @param alias alias of the stored key
     * @param pwdArray password for the stored key
     * @return {@link Optional} empty if key doesn't exist, key if it does
     */
    public Optional<Key> getKey(String alias, char[] pwdArray) {
        KeyStore keyStore = configContainer.getKeyStore();
        try {
            return Optional.ofNullable(keyStore.getKey(alias, pwdArray));
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            logger.debug("Could not retrieve the key {} from keystore, check the exception: {}", alias, e.getMessage());
            return Optional.empty();
        } catch (UnrecoverableKeyException e) {
            logger.debug("Possibly wrong password, check: {}", e.getMessage());
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            logger.debug(e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Check if a key with the given alias exists
     *
     * @param alias alias of the stored key
     * @return true if key exists, false if not
     */
    public boolean keyExists(String alias) {
        KeyStore keyStore = configContainer.getKeyStore();
        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            logger.debug("Something went wrong, check: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Delete a stored key from the keystore
     *
     * @param alias alias of the stored key
     */
    public void deleteKey(String alias) {
        KeyStore keyStore = configContainer.getKeyStore();
        try {
            keyStore.deleteEntry(alias);
        } catch (KeyStoreException e) {
            logger.debug("Could not delete the key {}, check: {}", alias, e.getMessage());
        }
    }
}
