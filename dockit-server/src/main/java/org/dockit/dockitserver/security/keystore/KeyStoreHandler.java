package org.dockit.dockitserver.security.keystore;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.dockit.dockitserver.config.ConfigContainer;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.security.key.KeyConstants;
import org.dockit.dockitserver.services.templates.AgentService;
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
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class to handle key store operations
 */
@Component
public class KeyStoreHandler {

    private static final Logger logger = LoggerFactory.getLogger(KeyStoreHandler.class);
    private final ConfigContainer configContainer;
    private final AgentService agentService;

    private final Map<String, ImmutablePair<Key, String>> keyCache;

    /**
     * @param configContainer {@link ConfigContainer} object to be injected
     */
    @Autowired
    public KeyStoreHandler(ConfigContainer configContainer, AgentService agentService) {
        this.configContainer = configContainer;
        this.agentService = agentService;
        keyCache = new ConcurrentHashMap<>();
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
            KeyStoreManager.saveKeyStore(keyStore);
            // Cache the key after saving it in the keystore
            keyCache.put(alias, new ImmutablePair<>(secretKey, String.valueOf(pwdArray)));
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
            if (pwdArray == null) {
                return Optional.empty();
            }
            // Check if the cache contains the key first to avoid the expensive keyStore.getKey() operation
            ImmutablePair<Key, String> secretKey = keyCache.get(alias);
            if (secretKey == null) {
                return Optional.empty();
            }
            // Check if the given password for key matches with the cached value
            if (secretKey.getValue().equals(String.valueOf(pwdArray))) {
                return Optional.of(secretKey.getKey());
            }

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
            KeyStoreManager.saveKeyStore(keyStore);
            keyCache.remove(alias);
        } catch (KeyStoreException e) {
            logger.debug("Could not delete the key {}, check: {}", alias, e.getMessage());
        }
    }

    /**
     * Initialise the keystore cache with existing keys from the keystore after startup
     */
    public void initialiseCache() {
        try {
            Enumeration<String> aliases = configContainer.getKeyStore().aliases();
            KeyStore keyStore = configContainer.getKeyStore();
            while (aliases.asIterator().hasNext()) {
                String keyAlias = aliases.nextElement();
                if (keyAlias.equals(configContainer.getConfig().getJwtSecretAlias())
                        || keyAlias.equals(KeyConstants.DB_KEY_ALIAS)) {
                    Key key = keyStore.getKey(keyAlias, "".toCharArray());
                    keyCache.put(keyAlias, new ImmutablePair<>(key, ""));
                    continue;
                }
                Optional<Agent> agent = agentService.findById(UUID.fromString(keyAlias));
                if (agent.isEmpty()) {
                    logger.info("Mismatch between saved agent keys and agents" +
                            " stored in the database, check the integrity of the database!");
                    continue;
                }
                Agent retrievedAgent = agent.get();
                Key key = keyStore.getKey(keyAlias, retrievedAgent.getPassword().toCharArray());
                keyCache.put(keyAlias, new ImmutablePair<>(key, retrievedAgent.getPassword()));
            }
        } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
            logger.debug("Something went wrong while initialising the keystore cache, check: {}", e.getMessage());
        }
    }
}
