package org.dockit.dockitserver.authentication.authentication;

import com.nimbusds.jwt.JWT;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Authentication object to be saved when jwt authentication mechanism is used.
 */
public class JwtAuthentication extends AbstractAuthenticationToken {
    private final JWT jwt;

    /**
     * @param jwt Set jwt used for the authentication
     * @param authorities Set the authority of the admin for the application
     */
    public JwtAuthentication(JWT jwt, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwt = jwt;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return jwt;
    }
}
