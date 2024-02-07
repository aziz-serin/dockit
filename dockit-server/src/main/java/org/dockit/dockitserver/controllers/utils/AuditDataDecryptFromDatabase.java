package org.dockit.dockitserver.controllers.utils;

import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.exceptions.encryption.EncryptionException;
import org.dockit.dockitserver.exceptions.security.key.KeyStoreException;
import org.dockit.dockitserver.security.encryption.AESCBCEncryptor;
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
import java.util.List;
import java.util.Optional;

/**
 * Decrypting tool for the database {@link Audit} entries.
 */
@Component
public class AuditDataDecryptFromDatabase {
    private static final Logger logger = LoggerFactory.getLogger(AuditDataDecryptFromDatabase.class);
    private final KeyStoreHandler keyStoreHandler;


    /**
     * @param keyStoreHandler {@link KeyStoreHandler} object to be injected
     */
    public AuditDataDecryptFromDatabase(KeyStoreHandler keyStoreHandler) {
        this.keyStoreHandler = keyStoreHandler;
    }

    /**
     * Decrypt the data field of each given {@link Audit} object
     *
     * @param audits list of {@link Audit} data
     * @return list of {@link Audit} with decrypted data fields
     * @throws EncryptionException when the data cannot be decrypted
     * @throws KeyStoreException when a database key is missing
     */
    public List<Audit> decryptAudits(List<Audit> audits) throws EncryptionException, KeyStoreException {
        Optional<Key> key = keyStoreHandler.getKey(KeyConstants.DB_KEY_ALIAS, "".toCharArray());
        if (key.isEmpty()) {
            logger.error("Could not find the database key in the keystore!");
            throw new KeyStoreException("Missing key");
        }
        List<Audit> decryptedAudits = audits.stream()
                .map(audit -> {
                    try {
                        return decryptAudit(audit, (SecretKey) key.get());
                    } catch (EncryptionException e) {
                        return null;
                    }
                })
                .toList();
        if (decryptedAudits.contains(null)) {
            throw new EncryptionException("Could not decrypt the given data!");
        }
        return decryptedAudits;
    }

    /**
     * Decrypt the data field of a given {@link Audit} object
     *
     * @param audit {@link Audit} data
     * @return {@link Audit} with decrypted data fields
     * @throws EncryptionException when the data cannot be decrypted
     * @throws KeyStoreException when a database key is missing
     */
    public Audit decryptAudit(Audit audit) throws EncryptionException, KeyStoreException {
        Optional<Key> key = keyStoreHandler.getKey(KeyConstants.DB_KEY_ALIAS, "".toCharArray());
        if (key.isEmpty()) {
            logger.error("Could not find the database key in the keystore!");
            throw new KeyStoreException("Missing key");
        }
        return decryptAudit(audit, (SecretKey) key.get());
    }

    private Audit decryptAudit(Audit audit, SecretKey key) throws EncryptionException {
        try {
            String decryptedData = AESCBCEncryptor.decrypt(audit.getData(), key);
            audit.setData(decryptedData);
            return audit;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException | IllegalArgumentException e) {
            throw new EncryptionException(e.getMessage());
        }
    }
}
