package org.dockit.dockitserver.controllers.utils;

import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.security.encryption.AESGCMEncryptor;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WriteProcessorTest {
    private static final UUID AGENT_ID = UUID.randomUUID();
    private static final String AGENT_PASSWORD = "password";
    private static final String DATA = "data to encrypt";
    private String encryptedData;

    private Optional<Key> agentSecretKey;
    private Optional<Key> dbSecretKey;

    @Mock
    private Agent agent;

    @Mock
    @MockBean
    private KeyStoreHandler keyStoreHandler;

    @InjectMocks
    private WriteProcessor writeProcessor;

    @Before
    public void setup() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        agentSecretKey = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.JWT_KEY_SIZE);
        dbSecretKey = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.JWT_KEY_SIZE);
        encryptedData = AESGCMEncryptor.encrypt(DATA, AGENT_ID.toString(), (SecretKey) agentSecretKey.get());
    }

    @Test
    public void writeProcessorReturnsEmptyGivenAgentKeyNotFound() {
        when(agent.getId()).thenReturn(AGENT_ID);
        when(agent.getPassword()).thenReturn(AGENT_PASSWORD);
        when(keyStoreHandler.getKey(eq(AGENT_ID.toString()), eq(AGENT_PASSWORD.toCharArray())))
                .thenReturn(Optional.empty());

        Optional<String> data = writeProcessor.process(agent, DATA);

        assertThat(data).isEmpty();
    }

    @Test
    public void writeProcessorReturnsEmptyGivenDbKeyIsNotFound() {
        when(agent.getId()).thenReturn(AGENT_ID);
        when(agent.getPassword()).thenReturn(AGENT_PASSWORD);
        when(keyStoreHandler.getKey(eq(AGENT_ID.toString()), eq(AGENT_PASSWORD.toCharArray())))
                .thenReturn(agentSecretKey);
        when(keyStoreHandler.getKey(KeyConstants.DB_KEY_ALIAS, "".toCharArray()))
                .thenReturn(Optional.empty());

        Optional<String> data = writeProcessor.process(agent, encryptedData);

        assertThat(data).isEmpty();
    }

    @Test
    public void writeProcessorReturnsEncryptedData() {
        when(agent.getId()).thenReturn(AGENT_ID);
        when(agent.getPassword()).thenReturn(AGENT_PASSWORD);
        when(keyStoreHandler.getKey(eq(AGENT_ID.toString()), eq(AGENT_PASSWORD.toCharArray())))
                .thenReturn(agentSecretKey);
        when(keyStoreHandler.getKey(KeyConstants.DB_KEY_ALIAS, "".toCharArray()))
                .thenReturn(dbSecretKey);

        Optional<String> data = writeProcessor.process(agent, encryptedData);

        assertThat(data).isPresent();
    }
}
