package org.dockit.dockitserver.authentication.services;

import jakarta.servlet.http.HttpServletRequest;
import org.dockit.dockitserver.authentication.authentication.APIKeyAuthentication;
import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.services.templates.APIKeyService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Authentication service responsible for the generation of {@link APIKeyAuthentication} object for API key
 * authentication
 */
@Component
public class APIKeyAuthenticationService {
    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
    private static APIKeyService apiKeyService;

    /**
     * @param apiKeyService {@link APIKeyService} object to be injected
     */
    public APIKeyAuthenticationService(APIKeyService apiKeyService) {
        APIKeyAuthenticationService.apiKeyService = apiKeyService;
    }

    /**
     * Extracts the apikey from the request headers, and validates it
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
}
