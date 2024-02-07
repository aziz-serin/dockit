package org.dockit.dockitserver.security.key;

import org.dockit.dockitserver.exceptions.security.key.KeyStoreException;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Optional;

/**
 * Component to aid the generation of key generation for specific purposes
 */
@Component
public class KeyHandler {
    private final KeyStoreHandler keyStoreHandler;

    /**
     * @param keyStoreHandler {@link KeyStoreHandler} object to be injected
     */
    @Autowired
    public KeyHandler(KeyStoreHandler keyStoreHandler) {
        this.keyStoreHandler = keyStoreHandler;
    }

    /**
     * Generate and save an AES password-based key for the agent
     *
     * @param alias alias to be used in the keystore for the key
     * @param password password for the given key
     * @return {@link Key} generated key
     * @throws KeyStoreException if generated key is empty or the key cannot be saved
     */
    public Key generateKeyForAgentAndSave(String alias, String password) throws KeyStoreException {
        Optional<Key> key = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.ENCRYPTION_KEY_SIZE, password);
        if (key.isEmpty()) {
            throw new KeyStoreException("Something went wrong creating the key!");
        }
        boolean saved = keyStoreHandler.saveKey(alias, (SecretKey) key.get(), password.toCharArray());
        if (!saved) {
            throw new KeyStoreException("Something went wrong saving the key!");
        }
        return key.get();
    }

    /**
     * Generate and save an AES key for database encryption
     *
     * @param alias alias to be used in the keystore for the key
     * @param password password for the given key to be stored in the keystore
     * @throws KeyStoreException if generated key is empty or the key cannot be saved
     */
    public void generateKeyForDBEncryption(String alias, String password) throws KeyStoreException {
        Optional<Key> key = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.ENCRYPTION_KEY_SIZE);
        if (key.isEmpty()) {
            throw new KeyStoreException("Something went wrong creating the key!");
        }
        boolean saved = keyStoreHandler.saveKey(alias, (SecretKey) key.get(), password.toCharArray());
        if (!saved) {
            throw new KeyStoreException("Something went wrong saving the key!");
        }
    }
}
