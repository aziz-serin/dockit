package org.dockit.dockitserver.controllers.utils;

import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.exceptions.encryption.EncryptionException;
import org.dockit.dockitserver.exceptions.security.key.KeyStoreException;
import org.dockit.dockitserver.security.encryption.AESCBCEncryptor;
import org.dockit.dockitserver.security.key.AESKeyGenerator;
import org.dockit.dockitserver.security.key.KeyConstants;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuditDataDecryptFromDatabaseTest {
    private static final String DATA = "data to encrypt";
    private static final String VM_ID = "vmId";
    private static final String CATEGORY = "category";

    @Mock
    @MockBean
    private KeyStoreHandler keyStoreHandler;
    @InjectMocks
    private AuditDataDecryptFromDatabase auditDataDecryptFromDatabase;

    private Optional<Key> dbSecretKey;
    private Audit audit;
    // This has undecryptable data as its data to check exception handling
    private Audit invalidAudit;

    @Before
    public void setup() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        dbSecretKey = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.JWT_KEY_SIZE);
        String encryptedData = AESCBCEncryptor.encrypt(DATA, (SecretKey) dbSecretKey.get());

        audit = EntityCreator.createAudit(VM_ID, CATEGORY, LocalDateTime.now(), encryptedData).get();
        invalidAudit = EntityCreator.createAudit(VM_ID, CATEGORY, LocalDateTime.now(), DATA).get();
    }

    @Test
    public void decryptAuditsFailsGivenKeyNotExisting() {
        when(keyStoreHandler.getKey(eq(KeyConstants.DB_KEY_ALIAS), eq("".toCharArray())))
                .thenReturn(Optional.empty());

        assertThrows(KeyStoreException.class, () -> {
            auditDataDecryptFromDatabase.decryptAudits(List.of(audit));
        });
    }

    @Test
    public void decryptAuditsFailsGivenInvalidlyEncryptedAudit() {
        when(keyStoreHandler.getKey(eq(KeyConstants.DB_KEY_ALIAS), eq("".toCharArray())))
                .thenReturn(dbSecretKey);

        assertThrows(EncryptionException.class, () -> {
            auditDataDecryptFromDatabase.decryptAudits(List.of(invalidAudit));
        });
    }

    @Test
    public void decryptAuditsSucceeds() throws EncryptionException, KeyStoreException {
        when(keyStoreHandler.getKey(eq(KeyConstants.DB_KEY_ALIAS), eq("".toCharArray())))
                .thenReturn(dbSecretKey);

        List<Audit> audits = auditDataDecryptFromDatabase.decryptAudits(List.of(audit));
        assertThat(audits).hasSize(1);
        assertThat(audits.get(0).getData()).isEqualTo(DATA);
    }

    @Test
    public void decryptAuditFailsGivenKeyNotExisting() {
        when(keyStoreHandler.getKey(eq(KeyConstants.DB_KEY_ALIAS), eq("".toCharArray())))
                .thenReturn(Optional.empty());

        assertThrows(KeyStoreException.class, () -> {
            auditDataDecryptFromDatabase.decryptAudit(audit);
        });
    }

    @Test
    public void decryptAuditFailsGivenInvalidlyEncryptedAudit() {
        when(keyStoreHandler.getKey(eq(KeyConstants.DB_KEY_ALIAS), eq("".toCharArray())))
                .thenReturn(dbSecretKey);

        assertThrows(EncryptionException.class, () -> {
            auditDataDecryptFromDatabase.decryptAudit(invalidAudit);
        });
    }

    @Test
    public void decryptAuditSucceeds() throws EncryptionException, KeyStoreException {
        when(keyStoreHandler.getKey(eq(KeyConstants.DB_KEY_ALIAS), eq("".toCharArray())))
                .thenReturn(dbSecretKey);

        Audit decryptedAudit = auditDataDecryptFromDatabase.decryptAudit(audit);
        assertThat(decryptedAudit.getData()).isEqualTo(DATA);
    }
}
