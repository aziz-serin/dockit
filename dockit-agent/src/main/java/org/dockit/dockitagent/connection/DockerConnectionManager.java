package org.dockit.dockitagent.connection;

import com.google.inject.Inject;
import org.dockit.dockitagent.config.ConfigContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * Manage interaction with the Docker Engine API. See the link for the API documentation
 * <a href="https://docs.docker.com/engine/api/v1.43/#tag/Container">here</a>
 */
public class DockerConnectionManager implements ConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(DockerConnectionManager.class);

    private static final String PING_ENDPOINT = "/_ping";

    private final ConfigContainer configContainer;

    /**
     * @param configContainer {@link ConfigContainer} instance to be injected
     */
    @Inject
    public DockerConnectionManager(ConfigContainer configContainer) {
        this.configContainer = configContainer;
    }

    /**
     * Check if the docker engine api is accessible
     *
     * @return true if the ping endpoint returns 200, false otherwise
     */
    public boolean isAlive() {
        try {
            HttpResponse<String> response = getResponse(PING_ENDPOINT, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                logger.info("Connection established with host {}", configContainer.getConfig().getDOCKER_URL());
                return true;
            } else {
                logger.info("Connection could not be established with host {}",
                        configContainer.getConfig().getDOCKER_URL());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.toString());
            return false;
        }
    }

    /**
     * Send the specified get request to the docker engine api
     *
     * @return response body in string format if OK response is received, empty if there was an error.
     */
    public Optional<String> sendRequest(String endpoint) {
        try {
            HttpResponse<String> response = getResponse(endpoint, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.info(response.body());
                return Optional.empty();
            }
            return Optional.of(response.body());
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    private <T> HttpResponse<T> getResponse(String endpoint, HttpResponse.BodyHandler<T> bodyHandler)
            throws IOException, InterruptedException {
        String url = configContainer.getConfig().getDOCKER_URL();

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(URI.create(url + endpoint))
                .GET()
                .header("accept", "application/json")
                .build();

        return client.send(request, bodyHandler);
    }
}
