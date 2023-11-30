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

@Component
public class KeyStoreHandler {

    private static final Logger logger = LoggerFactory.getLogger(KeyStoreHandler.class);
    private final ConfigContainer configContainer;

    @Autowired
    public KeyStoreHandler(ConfigContainer configContainer) {
        this.configContainer = configContainer;
    }

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

    public boolean keyExists(String alias) {
        KeyStore keyStore = configContainer.getKeyStore();
        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            logger.debug("Something went wrong, check: {}", e.getMessage());
            return false;
        }
    }

    public void deleteKey(String alias) {
        KeyStore keyStore = configContainer.getKeyStore();
        try {
            keyStore.deleteEntry(alias);
        } catch (KeyStoreException e) {
            logger.debug("Could not delete the key {}, check: {}", alias, e.getMessage());
        }
    }
}
