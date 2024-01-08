package org.dockit.dockitserver.authentication.services;

import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import org.dockit.dockitserver.authentication.authentication.JwtAuthentication;
import org.dockit.dockitserver.security.jwt.JWTValidator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class JwtAuthenticationService {
    private static final String AUTH_TOKEN_HEADER_NAME = "Authorization";
    private static JWTValidator jwtValidator;

    public JwtAuthenticationService(JWTValidator jwtValidator) {
        JwtAuthenticationService.jwtValidator = jwtValidator;
    }

    public static Authentication getAuthentication(HttpServletRequest request) throws ParseException {
        String jwt = request.getHeader(AUTH_TOKEN_HEADER_NAME);

        if (jwt == null || !jwtValidator.validateJwtToken(jwt))
            throw new BadCredentialsException("Invalid JWT!");

        return new JwtAuthentication(JWTParser.parse(jwt), AuthorityUtils.NO_AUTHORITIES);
    }
}
