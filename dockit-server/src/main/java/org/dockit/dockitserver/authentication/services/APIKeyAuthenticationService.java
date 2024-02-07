package org.dockit.dockitserver.authentication.services;

import jakarta.servlet.http.HttpServletRequest;
import org.dockit.dockitserver.authentication.authentication.APIKeyAuthentication;
import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.services.templates.APIKeyService;
import org.dockit.dockitserver.services.templates.AgentService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Authentication service responsible for the generation of {@link APIKeyAuthentication} object for API key
 * authentication
 */
@Component
public class APIKeyAuthenticationService {
    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
    private static APIKeyService apiKeyService;
    private static AgentService agentService;

    /**
     * @param apiKeyService {@link APIKeyService} object to be injected
     * @param agentService {@link AgentService} object to be injected
     */
    public APIKeyAuthenticationService(APIKeyService apiKeyService, AgentService agentService) {
        APIKeyAuthenticationService.apiKeyService = apiKeyService;
        APIKeyAuthenticationService.agentService = agentService;
    }

    /**
     * Extracts the apikey from the request headers, validates it, and updates last seen time of
     * agent if the provided apikey is valid
     *
     * @param request Incoming http request
     * @return {@link APIKeyAuthentication} if successfully authenticated
     * @throws BadCredentialsException if given apikey is not valid
     */
    public static Authentication getAuthentication(HttpServletRequest request) throws BadCredentialsException {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if (apiKey == null || !validAPIKey(apiKey)) {
            throw new BadCredentialsException("Invalid API Key!");
        }
        // Update the last active time before authentication is created and after an api key is validated.
        updateLastActiveTimeForAgent(apiKey);
        return new APIKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }

    private static boolean validAPIKey(String apiKey) {
        List<APIKey> keys = apiKeyService.findAll();

        return keys.stream()
                .map(APIKey::getToken)
                .anyMatch(key -> {
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    return passwordEncoder.matches(apiKey, key);
                });
    }

    private static void updateLastActiveTimeForAgent(String apiKey) {
        List<APIKey> keys = apiKeyService.findAll();
        // This method will be called after validating the key, so just getting the agent is fine
        Agent agent = keys.stream()
                .filter(key -> {
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    return passwordEncoder.matches(apiKey, key.getToken());
                }).map(APIKey::getAgent)
                .toList()
                .get(0);
        // Update the last active time of the agent with now
        agentService.updateLastActiveTime(agent.getId(), LocalDateTime.now());
    }
}
