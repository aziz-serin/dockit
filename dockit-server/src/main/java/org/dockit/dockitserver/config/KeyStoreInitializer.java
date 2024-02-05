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

@Component
public class KeyStoreInitializer {

    @Autowired
    public KeyStoreInitializer(ConfigContainer configContainer, KeyStoreHandler keyStoreHandler, KeyHandler keyHandler)
            throws KeyStoreException {
        // Generate key to be used in jwt generation
        Key jwt_key = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.JWT_KEY_SIZE).get();
        keyStoreHandler.saveKey(configContainer.getConfig().getJwtSecretAlias(), (SecretKey) jwt_key, "".toCharArray());

        // Generate key to be used in symmetric encryption for data saved and save it in keystore
        keyHandler.generateKeyForDBEncryption(KeyConstants.DB_KEY_ALIAS, "");
    }
}
