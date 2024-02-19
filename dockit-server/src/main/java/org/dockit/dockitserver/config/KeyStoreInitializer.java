package org.dockit.dockitserver.config;

import org.dockit.dockitserver.exceptions.security.key.KeyStoreException;
import org.dockit.dockitserver.security.key.AESKeyGenerator;
import org.dockit.dockitserver.security.key.KeyConstants;
import org.dockit.dockitserver.security.key.KeyHandler;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;

/**
 * Component for the initialization of the keystore
 */
@Component
public class KeyStoreInitializer {
    /**
     * Generate and save keys for jwt signing and database encryption.
     *
     * @param configContainer {@link ConfigContainer} object containing the keystore and the config.
     * @param keyStoreHandler {@link KeyStoreHandler} for key operations
     * @param keyHandler {@link KeyHandler} for the generation of keys
     * @throws KeyStoreException if generation of a key fails
     */
    @Autowired
    public KeyStoreInitializer(ConfigContainer configContainer, KeyStoreHandler keyStoreHandler, KeyHandler keyHandler)
            throws KeyStoreException {
        String jwtSecretAlias = configContainer.getConfig().getJwtSecretAlias();
        if (!keyStoreHandler.keyExists(jwtSecretAlias)) {
            // Generate key to be used in jwt generation
            Key jwt_key = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.JWT_KEY_SIZE).get();
            keyStoreHandler.saveKey(jwtSecretAlias, (SecretKey) jwt_key, "".toCharArray());
        }
        if(!keyStoreHandler.keyExists(KeyConstants.DB_KEY_ALIAS)) {
            // Generate key to be used in symmetric encryption for data saved and save it in keystore
            keyHandler.generateKeyForDBEncryption(KeyConstants.DB_KEY_ALIAS, "");
        }
    }
}
