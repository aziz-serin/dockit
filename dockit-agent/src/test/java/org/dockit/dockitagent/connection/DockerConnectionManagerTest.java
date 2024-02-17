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
// Uncomment below to ignore this test case if you do not have a docker engine api running or you are not interested in
// docker running functionality
//@Ignore("Ignored because docker engine not available/not used")
public class DockerConnectionManagerTest {

    private static final String DOCKER_URL = "http://localhost:2375";
    private static final String FAKE_URL = "http://someurl:7545";
    private static final String SOME_ENDPOINT = "/containers/json";
    private static final String FAKE_ENDPOINT = "/fake";

    @Mock
    private ConfigContainer configContainer;

    @Mock
    private Config config;

    @Test
    public void isAliveFailsGivenNon200Response() {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getDOCKER_URL()).thenReturn(FAKE_URL);

        DockerConnectionManager dockerConnectionManager = new DockerConnectionManager(configContainer);

        assertFalse(dockerConnectionManager.isAlive());
    }

    @Test
    public void isAliveSucceeds() {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getDOCKER_URL()).thenReturn(DOCKER_URL);

        DockerConnectionManager dockerConnectionManager = new DockerConnectionManager(configContainer);

        assertTrue(dockerConnectionManager.isAlive());
    }

    @Test
    public void sendRequestReturnsEmptyGivenFailedRequest() {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getDOCKER_URL()).thenReturn(DOCKER_URL);

        DockerConnectionManager dockerConnectionManager = new DockerConnectionManager(configContainer);

        assertThat(dockerConnectionManager.sendRequest(FAKE_ENDPOINT)).isEmpty();
    }

    @Test
    public void sendRequestReturnsRequestBody() {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getDOCKER_URL()).thenReturn(DOCKER_URL);

        DockerConnectionManager dockerConnectionManager = new DockerConnectionManager(configContainer);

        assertThat(dockerConnectionManager.sendRequest(SOME_ENDPOINT)).isPresent();
    }
}
