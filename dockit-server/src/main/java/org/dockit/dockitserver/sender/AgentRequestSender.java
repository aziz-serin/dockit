package org.dockit.dockitserver.sender;

import org.dockit.dockitserver.entities.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * Communication class to send the requests to the given agent
 */
@Component
public class AgentRequestSender {

    private static final Logger logger = LoggerFactory.getLogger(AgentRequestSender.class);

    private final AgentRequestPreparer agentRequestPreparer;

    /**
     * @param agentRequestPreparer {@link AgentRequestPreparer} instance to be injected
     */
    @Autowired
    public AgentRequestSender(AgentRequestPreparer agentRequestPreparer) {
        this.agentRequestPreparer = agentRequestPreparer;
    }

    /**
     * Method to send intrusion request for to the agent
     *
     * @param agent {@link Agent} to send the request to
     * @param userName userName of the detected intruder
     * @return true if request was successful, false if not
     */
    public boolean sendIntrusionRequest(Agent agent, String userName) {
        HttpClient client = HttpClient.newHttpClient();

        Optional<HttpRequest> request = agentRequestPreparer.prepareIntrusionRequest(agent, userName);
        if (request.isEmpty()) {
            logger.debug("Could not construct the request to be sent for the detected intrusion");
            return false;
        }

        try {
            HttpResponse<String> response = client.send(request.get(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.error("Could not send the request for the intrusion command to the agent," +
                        " {}, see the response: {}", agent.getId(), response.body());
                return false;
            }
            logger.info("Sent the request for the intrusion command to the agent, {}", agent.getId());
            return true;
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * See if the agent server is alive
     *
     * @param agent {@link Agent} to send the request to
     * @return true if agent server is alive, false otherwise
     */
    public boolean isAgentServerAlive(Agent agent) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(URI.create(agent.getAgentUrl()
                        + AgentRequestConstants.PING_ENDPOINT))
                .GET()
                .header("accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.error("Agent server {} is not alive!", agent.getAgentUrl());
                return false;
            }
            logger.info("Agent server {} is alive!", agent.getAgentUrl());
            return true;
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
