package org.dockit.dockitagent.entity;

import org.dockit.dockitagent.config.Config;
import org.dockit.dockitagent.config.templates.Container;
import org.dockit.dockitagent.encryption.AESGCMEncrypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuditConstructorTest {
    private static final String DATA = "some_data";
    private static final String ENCRYPTED_DATA = "encrypted_data";
    private static final String CATEGORY = "category";
    private static final String VM_ID = "vm_id";

    @Mock
    private Container container;
    @Mock
    private Config config;
    @Mock
    private AESGCMEncrypt encryptor;

    @Test
    public void constructReturnsEmptyGivenEncryptorException() throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException {
        when(encryptor.encrypt(anyString())).thenThrow(NoSuchAlgorithmException.class);

        AuditConstructor constructor = new AuditConstructor(encryptor, container);

        assertThat(constructor.construct(DATA, CATEGORY)).isEmpty();
    }

    @Test
    public void constructReturnsEmptyGivenAuditBuilderException() throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException {
        when(encryptor.encrypt(DATA)).thenReturn(ENCRYPTED_DATA);
        when(container.getConfig()).thenReturn(config);
        when(config.getZONE_ID()).thenReturn(ZoneId.systemDefault());
        when(config.getVM_ID()).thenReturn(VM_ID);

        AuditConstructor constructor = new AuditConstructor(encryptor, container);

        // null for category to get AuditBuildingException
        assertThat(constructor.construct(DATA, null)).isEmpty();
    }

    @Test
    public void constructReturnsAudit() throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException {
        when(encryptor.encrypt(DATA)).thenReturn(ENCRYPTED_DATA);
        when(container.getConfig()).thenReturn(config);
        when(config.getZONE_ID()).thenReturn(ZoneId.systemDefault());
        when(config.getVM_ID()).thenReturn(VM_ID);

        AuditConstructor constructor = new AuditConstructor(encryptor, container);

        Optional<Audit> audit = constructor.construct(DATA, CATEGORY);

        assertThat(audit).isPresent();
        assertThat(audit.get().getCategory()).isEqualTo(CATEGORY);
        assertThat(audit.get().getData()).isEqualTo(ENCRYPTED_DATA);
        assertThat(audit.get().getVmId()).isEqualTo(VM_ID);
        assertThat(audit.get().getTimeStamp().getZone()).isEqualTo(ZoneId.systemDefault());
    }
}
