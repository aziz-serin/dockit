package org.dockit.dockitserver.security.key;

import org.dockit.dockitserver.exceptions.security.key.KeyStoreException;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KeyHandlerTest {

    private static final String ALIAS = "alias";
    private static final String PASSWORD = "password";
    private static final String CREATION_EXCEPTION_MESSAGE = "Something went wrong creating the key!";
    private static final String SAVING_EXCEPTION_MESSAGE = "Something went wrong saving the key!";

    @Mock
    @MockBean
    private KeyStoreHandler keyStoreHandler;

    @InjectMocks
    private KeyHandler keyHandler;

    @Test
    public void generateKeyForAgentAndSaveFailsGivenNullPassword() {
        Exception exception = assertThrows(KeyStoreException.class, () -> {
            keyHandler.generateKeyForAgentAndSave(ALIAS, null);
        });

        assertThat(CREATION_EXCEPTION_MESSAGE).isEqualTo(exception.getMessage());
    }

    @Test
    public void generateKeyForAgentAndSaveFailsGivenFailureInSaving() {
        when(keyStoreHandler.saveKey(any(String.class), any(SecretKey.class), any(char[].class))).thenReturn(false);

        Exception exception = assertThrows(KeyStoreException.class, () -> {
            keyHandler.generateKeyForAgentAndSave(ALIAS, PASSWORD);
        });

        assertThat(SAVING_EXCEPTION_MESSAGE).isEqualTo(exception.getMessage());
    }

    @Test
    public void generateKeyForAgentAndSaveSucceeds() throws KeyStoreException {
        when(keyStoreHandler.saveKey(any(String.class), any(SecretKey.class), any(char[].class))).thenReturn(true);

        keyHandler.generateKeyForAgentAndSave(ALIAS, PASSWORD);
    }

    @Test
    public void generateKeyForDBEncryptionFailsGivenFailureInSaving() {
        when(keyStoreHandler.saveKey(any(String.class), any(SecretKey.class), any(char[].class))).thenReturn(false);

        Exception exception = assertThrows(KeyStoreException.class, () -> {
            keyHandler.generateKeyForDBEncryption(ALIAS, PASSWORD);
        });

        assertThat(SAVING_EXCEPTION_MESSAGE).isEqualTo(exception.getMessage());
    }

    @Test
    public void generateKeyForDBEncryptionAndSaveSucceeds() throws KeyStoreException {
        when(keyStoreHandler.saveKey(any(String.class), any(SecretKey.class), any(char[].class))).thenReturn(true);

        keyHandler.generateKeyForDBEncryption(ALIAS, PASSWORD);
    }
}
