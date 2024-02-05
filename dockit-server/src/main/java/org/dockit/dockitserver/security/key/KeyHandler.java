package org.dockit.dockitserver.security.key;

import org.dockit.dockitserver.exceptions.security.key.KeyStoreException;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Optional;

@Component
public class KeyHandler {
    private final KeyStoreHandler keyStoreHandler;

    @Autowired
    public KeyHandler(KeyStoreHandler keyStoreHandler) {
        this.keyStoreHandler = keyStoreHandler;
    }

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
