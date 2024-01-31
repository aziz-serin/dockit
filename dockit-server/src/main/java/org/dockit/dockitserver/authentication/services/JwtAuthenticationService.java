package org.dockit.dockitserver.authentication.services;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import org.dockit.dockitserver.authentication.authentication.JwtAuthentication;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.security.jwt.JWTValidator;
import org.dockit.dockitserver.services.templates.AdminService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Optional;

@Component
public class JwtAuthenticationService {
    private static final String AUTH_TOKEN_HEADER_NAME = "Authorization";
    private static JWTValidator jwtValidator;
    private static AdminService adminService;

    public JwtAuthenticationService(JWTValidator jwtValidator, AdminService adminService) {
        JwtAuthenticationService.jwtValidator = jwtValidator;
        JwtAuthenticationService.adminService = adminService;
    }

    public static Authentication getAuthentication(HttpServletRequest request) throws ParseException {
        String jwt = request.getHeader(AUTH_TOKEN_HEADER_NAME);

        if (jwt == null || !jwtValidator.validateJwtToken(jwt))
            throw new BadCredentialsException("Invalid JWT!");

        JWT jwtObject = JWTParser.parse(jwt);
        String username = jwtObject.getJWTClaimsSet().getSubject();
        Optional<Admin> admin = adminService.findByUsername(username);

        if (admin.isEmpty())
            throw new BadCredentialsException("Invalid JWT!");

        String role = admin.get().getPrivilege().toString();

        return new JwtAuthentication(jwtObject, AuthorityUtils.createAuthorityList(role));
    }
}
