package org.dockit.dockitserver.sender;

import org.dockit.dockitserver.entities.Agent;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.security.Key;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgentRequestPreparerTest {
    private static final String USER_NAME = "username";
    private static final UUID AGENT_ID = UUID.randomUUID();
    private static final String AGENT_PASSWORD = "password";
    private static final String AGENT_URL = "http://someurl.com";

    @Mock
    @MockBean
    private KeyStoreHandler keyStoreHandler;

    @InjectMocks
    private AgentRequestPreparer agentRequestPreparer;

    @Mock
    private Agent agent;

    private Optional<Key> secretKey;
    private URL agentUrl;

    @Before
    public void setup() throws MalformedURLException {
        secretKey = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.ENCRYPTION_KEY_SIZE);
        agentUrl = new URL(AGENT_URL);
    }

    @Test
    public void prepareReqeustReturnsEmptyGivenKeyIsEmpty() {
        when(keyStoreHandler.getKey(any(), any())).thenReturn(Optional.empty());
        when(agent.getId()).thenReturn(AGENT_ID);
        when(agent.getPassword()).thenReturn(AGENT_PASSWORD);

        Optional<HttpRequest> httpRequest = agentRequestPreparer.prepareIntrusionRequest(agent, USER_NAME);

        assertThat(httpRequest).isEmpty();
    }

    @Test
    public void prepareReqeustReturnsEmptyGivenErrorWithEncryption() {
        Optional<Key> invalidEncryptionKey = AESKeyGenerator.generateKey("DES",
                56);
        when(keyStoreHandler.getKey(any(), any())).thenReturn(invalidEncryptionKey);
        when(agent.getId()).thenReturn(AGENT_ID);
        when(agent.getPassword()).thenReturn(AGENT_PASSWORD);

        Optional<HttpRequest> httpRequest = agentRequestPreparer.prepareIntrusionRequest(agent, USER_NAME);

        assertThat(httpRequest).isEmpty();
    }

    @Test
    public void prepareRequestReturnsRequest() {
        when(keyStoreHandler.getKey(any(), any())).thenReturn(secretKey);
        when(agent.getId()).thenReturn(AGENT_ID);
        when(agent.getPassword()).thenReturn(AGENT_PASSWORD);
        when(agent.getAgentUrl()).thenReturn(agentUrl);

        Optional<HttpRequest> httpRequest = agentRequestPreparer.prepareIntrusionRequest(agent, USER_NAME);

        assertThat(httpRequest).isPresent();
        assertThat(httpRequest.get().method()).isEqualTo("POST");
    }

}
