package org.dockit.dockitagent.connection;

import org.dockit.dockitagent.config.Config;
import org.dockit.dockitagent.config.ConfigContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerConnectionManagerTest {
    private static final String SERVER_URL = "http://localhost:8080";
    private static final String FAKE_URL = "http://someurl:7545";
    private static final String SOME_ENDPOINT = "/api/write";
    private static final String API_KEY = "api_key";
    private static final String FAKE_ENDPOINT = "/fake";

    @Mock
    private ConfigContainer configContainer;

    @Mock
    private Config config;

    @Test
    public void isAliveFailsGivenNon200Response() {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getSERVER_URL()).thenReturn(FAKE_URL);

        ServerConnectionManager serverConnectionManager = new ServerConnectionManager(configContainer);

        assertFalse(serverConnectionManager.isAlive());
    }

    @Test
    public void isAliveSucceeds() {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getSERVER_URL()).thenReturn(SERVER_URL);

        ServerConnectionManager serverConnectionManager = new ServerConnectionManager(configContainer);

        assertTrue(serverConnectionManager.isAlive());
    }

    @Test
    public void sendRequestReturnsEmptyGivenFailedRequest() {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getSERVER_URL()).thenReturn(SERVER_URL);
        when(config.getAPI_KEY()).thenReturn(API_KEY);

        ServerConnectionManager serverConnectionManager = new ServerConnectionManager(configContainer);

        assertThat(serverConnectionManager.sendRequest(FAKE_ENDPOINT, "")).isEmpty();
    }
}
