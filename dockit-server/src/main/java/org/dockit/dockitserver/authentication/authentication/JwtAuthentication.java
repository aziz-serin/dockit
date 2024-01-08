package org.dockit.dockitserver.authentication.authentication;

import com.nimbusds.jwt.JWT;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthentication extends AbstractAuthenticationToken {
    private final JWT jwt;

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
