package org.dockit.dockitagent.connection;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import org.dockit.dockitagent.config.templates.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

/**
 * Manage interactions with the dockit-server application.
 */
public class ServerConnectionManager {
    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

    private final static Logger logger = LoggerFactory.getLogger(ServerConnectionManager.class);

    private final Container container;

    private static final String LIVENESS_ENDPOINT = "/actuator/health/liveness";

    /**
     * @param container {@link org.dockit.dockitagent.config.ConfigContainer} instance to be injected
     */
    @Inject
    public ServerConnectionManager(Container container) {
        this.container = container;
    }

    /**
     * Check if the server is accessible and ready to accept requests
     *
     * @return false if not ready or accessible, true otherwise
     */
    public boolean isAlive() {
        try {
            HttpResponse<String> response = getResponse(LIVENESS_ENDPOINT, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Gson gson = new Gson();
                Map<String, String> body = gson.fromJson(response.body(), Map.class);
                if (!body.get("status").equals("UP")) {
                    logger.info("Server is not ready, obtained status is: {}", body.get("status"));
                    return false;
                }
                logger.info("Connection established with host {}", container.getConfig().getSERVER_URL());
                return true;
            } else {
                logger.info("Connection could not be established with host {}",
                        container.getConfig().getDOCKER_URL());
                return false;
            }
        } catch (IOException | InterruptedException | JsonSyntaxException e) {
            logger.error(e.toString());
            return false;
        }
    }

    /**
     * Send the given POST request to the server's write endpoint
     *
     * @return response body in string format if OK response is received, empty if there was an error.
     */

    public Optional<String> sendRequest(String endPoint, String body) {
        try {
            HttpResponse<String> response = postResponse(endPoint, body, HttpResponse.BodyHandlers.ofString());
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
        String url = container.getConfig().getSERVER_URL();

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(URI.create(url + endpoint))
                .GET()
                .header("accept", "application/json")
                .build();

        return client.send(request, bodyHandler);
    }

    private <T> HttpResponse<T> postResponse(String endpoint, String body, HttpResponse.BodyHandler<T> bodyHandler)
            throws IOException, InterruptedException {
        String url = container.getConfig().getSERVER_URL();
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(URI.create(url +  endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .headers("accept", "application/json",
                        AUTH_TOKEN_HEADER_NAME, container.getConfig().getAPI_KEY())
                .build();

        return client.send(request, bodyHandler);
    }
}
