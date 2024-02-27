package org.dockit.dockitserver.sender;

import org.dockit.dockitserver.entities.Agent;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgentRequestSenderTest {
    private final String AGENT_URL = "http://localhost:4567";
    private final String SOME_URL = "http://someurl.com";
    private final String USER_NAME = "someuser";

    @Mock
    @MockBean
    private AgentRequestPreparer agentRequestPreparer;

    @InjectMocks
    private AgentRequestSender agentRequestSender;

    private UUID agentId;

    @BeforeAll
    public void setup() {
        agentId = UUID.randomUUID();
    }

    @Test
    //@Ignore("Enable this test if the agent is running at the same time with the given URL!")
    public void isAgentAliveReturnsTrueGivenAgentIsAlive() throws MalformedURLException {
        Agent agent = new Agent();
        URL agentUrl = new URL(AGENT_URL);
        agent.setAgentUrl(agentUrl);

        assertTrue(agentRequestSender.isAgentServerAlive(agent));
    }

    @Test
    public void isAgentAliveReturnsFalseGivenAgentIsNotAlive() throws MalformedURLException {
        Agent agent = new Agent();
        URL agentUrl = new URL(SOME_URL);
        agent.setAgentUrl(agentUrl);

        assertFalse(agentRequestSender.isAgentServerAlive(agent));
    }

    @Test
    public void sendIntrusionRequestReturnsFalseGivenEmptyRequest() throws MalformedURLException {
        when(agentRequestPreparer.prepareIntrusionRequest(any(), any())).thenReturn(Optional.empty());

        Agent agent = new Agent();
        agent.setId(agentId);
        URL agentUrl = new URL(SOME_URL);
        agent.setAgentUrl(agentUrl);

        assertFalse(agentRequestSender.sendIntrusionRequest(agent, USER_NAME));
    }

    @Test
    //@Ignore("Enable this test if the agent is running at the same time with the given URL!")
    public void sendIntrusionRequestReturnsTrueGivenRequestWasSentSuccessfully() throws MalformedURLException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(AGENT_URL
                        + AgentRequestConstants.PING_ENDPOINT))
                .GET()
                .header("accept", "application/json")
                .build();
        when(agentRequestPreparer.prepareIntrusionRequest(any(), any())).thenReturn(Optional.of(request));

        Agent agent = new Agent();
        agent.setId(agentId);
        URL agentUrl = new URL(AGENT_URL);
        agent.setAgentUrl(agentUrl);

        assertTrue(agentRequestSender.sendIntrusionRequest(agent, USER_NAME));
    }

    @Test
    public void sendIntrusionRequestReturnsFalse() throws MalformedURLException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(SOME_URL
                        + AgentRequestConstants.PING_ENDPOINT))
                .GET()
                .header("accept", "application/json")
                .build();
        when(agentRequestPreparer.prepareIntrusionRequest(any(), any())).thenReturn(Optional.of(request));

        Agent agent = new Agent();
        agent.setId(agentId);
        URL agentUrl = new URL(AGENT_URL);
        agent.setAgentUrl(agentUrl);

        assertFalse(agentRequestSender.sendIntrusionRequest(agent, USER_NAME));
    }
}
