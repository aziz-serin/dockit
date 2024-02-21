package org.dockit.dockitserver.security.keystore;

import org.dockit.dockitserver.config.ConfigContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.KeyStore;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KeyStoreHandlerTest {

    private static final String PASSWORD = "changeit";
    private static final String ALIAS = "alias";

    private KeyStore keyStore;
    private SecretKey key;

    @MockBean
    @Mock
    private ConfigContainer configContainer;

    @InjectMocks
    private KeyStoreHandler keyStoreHandler;


    @Before
    public void setup() throws Exception {
        // Init keystore
        keyStore = KeyStore.getInstance("JCEKS");
        char[] pwdArray = PASSWORD.toCharArray();
        keyStore.load(null, pwdArray);
        // Create an aes key to use as secret key while testing
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        key = keyGenerator.generateKey();
    }

    @Test
    public void saveKeyFailsGivenNullPassword() {
        when(configContainer.getKeyStore()).thenReturn(keyStore);

        assertFalse(keyStoreHandler.saveKey(ALIAS, key, null));
    }

    @Test
    public void saveKeySucceeds() {
        when(configContainer.getKeyStore()).thenReturn(keyStore);

        try (MockedStatic<KeyStoreManager> removerMock = Mockito.mockStatic(KeyStoreManager.class)) {
            removerMock.when(() -> KeyStoreManager.saveKeyStore(keyStore)).thenAnswer((Answer<Void>) invocation -> null);

            assertTrue(keyStoreHandler.saveKey(ALIAS, key, PASSWORD.toCharArray()));
            removerMock.verify(() -> KeyStoreManager.saveKeyStore(keyStore));
        }
    }

   @Test
   public void getKeyReturnsEmptyGivenKeyDoesNotExist() {
       when(configContainer.getKeyStore()).thenReturn(keyStore);

       Optional<Key> key = keyStoreHandler.getKey("no", "nope".toCharArray());

       assertTrue(key.isEmpty());
   }

    @Test
    public void getKeyReturnsEmptyGivenKeyExistsButPasswordNull() {
        when(configContainer.getKeyStore()).thenReturn(keyStore);

        try (MockedStatic<KeyStoreManager> removerMock = Mockito.mockStatic(KeyStoreManager.class)) {
            removerMock.when(() -> KeyStoreManager.saveKeyStore(keyStore)).thenAnswer((Answer<Void>) invocation -> null);

            assertTrue(keyStoreHandler.saveKey(ALIAS, key, PASSWORD.toCharArray()));
            removerMock.verify(() -> KeyStoreManager.saveKeyStore(keyStore));

            Optional<Key> savedKey = keyStoreHandler.getKey(ALIAS, null);
            assertTrue(savedKey.isEmpty());
        }
    }

    @Test
    public void getKeyReturnsEmptyGivenKeyExistsButPasswordIsWrong() {
        when(configContainer.getKeyStore()).thenReturn(keyStore);

        try (MockedStatic<KeyStoreManager> removerMock = Mockito.mockStatic(KeyStoreManager.class)) {
            removerMock.when(() -> KeyStoreManager.saveKeyStore(keyStore)).thenAnswer((Answer<Void>) invocation -> null);

            assertTrue(keyStoreHandler.saveKey(ALIAS, key, PASSWORD.toCharArray()));
            removerMock.verify(() -> KeyStoreManager.saveKeyStore(keyStore));

            Optional<Key> savedKey = keyStoreHandler.getKey(ALIAS, "wrong_password".toCharArray());

            assertTrue(savedKey.isEmpty());
        }
    }

    @Test
    public void getKeyReturnsKey() {
        when(configContainer.getKeyStore()).thenReturn(keyStore);

        try (MockedStatic<KeyStoreManager> removerMock = Mockito.mockStatic(KeyStoreManager.class)) {
            removerMock.when(() -> KeyStoreManager.saveKeyStore(keyStore)).thenAnswer((Answer<Void>) invocation -> null);

            assertTrue(keyStoreHandler.saveKey(ALIAS, key, PASSWORD.toCharArray()));
            removerMock.verify(() -> KeyStoreManager.saveKeyStore(keyStore));

            Optional<Key> savedKey = keyStoreHandler.getKey(ALIAS, PASSWORD.toCharArray());

            assertTrue(savedKey.isPresent());
        }
    }

    @Test
    public void keyExistsReturnsFalseGivenKeyDoesNotExist() {
        when(configContainer.getKeyStore()).thenReturn(keyStore);

        assertFalse(keyStoreHandler.keyExists(ALIAS));
    }

    @Test
    public void keyExistsReturnsTrueGivenKeyDoesNotExist() {
        when(configContainer.getKeyStore()).thenReturn(keyStore);

        try (MockedStatic<KeyStoreManager> removerMock = Mockito.mockStatic(KeyStoreManager.class)) {
            removerMock.when(() -> KeyStoreManager.saveKeyStore(keyStore)).thenAnswer((Answer<Void>) invocation -> null);

            assertTrue(keyStoreHandler.saveKey(ALIAS, key, PASSWORD.toCharArray()));
            removerMock.verify(() -> KeyStoreManager.saveKeyStore(keyStore));

            assertTrue(keyStoreHandler.keyExists(ALIAS));
        }
    }

    @Test
    public void deleteKeyDeletesKey() {
        when(configContainer.getKeyStore()).thenReturn(keyStore);
        try (MockedStatic<KeyStoreManager> removerMock = Mockito.mockStatic(KeyStoreManager.class)) {
            removerMock.when(() -> KeyStoreManager.saveKeyStore(keyStore)).thenAnswer((Answer<Void>) invocation -> null);

            assertTrue(keyStoreHandler.saveKey(ALIAS, key, PASSWORD.toCharArray()));
            removerMock.verify(() -> KeyStoreManager.saveKeyStore(keyStore));

            assertTrue(keyStoreHandler.keyExists(ALIAS));

            keyStoreHandler.deleteKey(ALIAS);

            assertFalse(keyStoreHandler.keyExists(ALIAS));
        }
    }
}
