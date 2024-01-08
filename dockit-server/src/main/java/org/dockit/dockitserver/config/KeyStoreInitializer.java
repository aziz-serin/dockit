package org.dockit.dockitserver.config;

import org.dockit.dockitserver.security.key.AESKeyGenerator;
import org.dockit.dockitserver.security.key.KeyConstants;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;

@Component
public class KeyStoreInitializer {

    @Autowired
    public KeyStoreInitializer(ConfigContainer configContainer, KeyStoreHandler keyStoreHandler) {
        Key key = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.JWT_KEY_SIZE).get();
        keyStoreHandler.saveKey(configContainer.getConfig().getJwtSecretAlias(), (SecretKey) key, "".toCharArray());
    }
}
