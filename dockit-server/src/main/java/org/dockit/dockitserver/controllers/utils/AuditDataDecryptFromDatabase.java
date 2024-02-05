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

@Component
public class AuditDataDecryptFromDatabase {
    private static final Logger logger = LoggerFactory.getLogger(AuditDataDecryptFromDatabase.class);
    private final KeyStoreHandler keyStoreHandler;

    public AuditDataDecryptFromDatabase(KeyStoreHandler keyStoreHandler) {
        this.keyStoreHandler = keyStoreHandler;
    }

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
            throw new EncryptionException("Could not encrypt the given data!");
        }
        return decryptedAudits;
    }

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
