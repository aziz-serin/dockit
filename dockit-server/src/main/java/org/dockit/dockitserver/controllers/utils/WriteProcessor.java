package org.dockit.dockitserver.controllers.utils;

import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.security.encryption.AESCBCEncryptor;
import org.dockit.dockitserver.security.encryption.AESGCMEncryptor;
import org.dockit.dockitserver.security.key.KeyConstants;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * Deal with the incoming requests from the {@link org.dockit.dockitserver.controllers.WriteController}
 */
@Component
public class WriteProcessor {
    private final KeyStoreHandler keyStoreHandler;

    private static final Logger logger = LoggerFactory.getLogger(WriteProcessor.class);

    /**
     * @param keyStoreHandler {@link KeyStoreHandler} to be injected
     */
    public WriteProcessor(KeyStoreHandler keyStoreHandler) {
        this.keyStoreHandler = keyStoreHandler;
    }

    /**
     * Takes the sender agent of the data and encrypted data, then decrypts it using the agent's key.
     *
     * @param agent {@link Agent} which sent the data
     * @param agentEncryptedData encrypted data sent from an agent
     * @return {@link Optional} empty if any error occurs, if not the sent data encrypted with the database key
     */
    public Optional<String> process(Agent agent, String agentEncryptedData) {

        Optional<Key> key = keyStoreHandler.getKey(agent.getId().toString(), agent.getPassword().toCharArray());
        if (key.isEmpty()) {
            return Optional.empty();
        }
        Optional<String> decryptedData =
                decryptData(agentEncryptedData, agent.getId().toString(), (SecretKey) key.get());
        if (decryptedData.isEmpty()) {
            return Optional.empty();
        }
        Optional<Key> dbKey = keyStoreHandler.getKey(KeyConstants.DB_KEY_ALIAS, "".toCharArray());
        if (dbKey.isEmpty()) {
            return Optional.empty();
        }
        return encryptData(decryptedData.get(), (SecretKey) dbKey.get());
    }

    private Optional<String> decryptData(String data, String aad, SecretKey key) {
        try {
            return Optional.of(AESGCMEncryptor.decrypt(data, aad, key));
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IllegalArgumentException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> encryptData(String data, SecretKey key) {
        try {
            return Optional.of(AESCBCEncryptor.encrypt(data, key));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IllegalArgumentException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }
}
