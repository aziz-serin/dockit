package org.dockit.dockitserver.authentication.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Authentication object to be added to context when API key method is used.
 */
public class APIKeyAuthentication extends AbstractAuthenticationToken {
    private final String apiKey;

    /**
     * @param apiKey Set apiKey used for the authentication
     * @param authorities No authorities for this method, it will always be empty since agents do not have privileges
     */
    public APIKeyAuthentication(String apiKey, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.apiKey = apiKey;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return apiKey;
    }
}
