package org.dockit.dockitserver.authentication.issuer;

import com.nimbusds.jose.JOSEException;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.security.jwt.JWTGenerator;
import org.dockit.dockitserver.services.templates.AdminService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Issuer class responsible for the generation of jwts
 */
@Component
public class JwtIssuer {

    private final AdminService adminService;
    private final JWTGenerator jwtGenerator;

    /**
     * @param adminService {@link AdminService} object to be injected
     * @param jwtGenerator {@link JWTGenerator} object to be injected
     */
    public JwtIssuer(AdminService adminService, JWTGenerator jwtGenerator) {
        this.adminService = adminService;
        this.jwtGenerator = jwtGenerator;
    }

    /**
     * @param username username for {@link Admin}
     * @param password password for {@link Admin}
     * @return {@link Optional} string containing the jwt if successful, empty if not
     */
    public Optional<String> issueJwt(String username, String password) {
        Optional<Admin> admin = adminService.findByUsername(username);
        if (admin.isEmpty()) {
            return Optional.empty();
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, admin.get().getPassword())) {
            return Optional.empty();
        }
        try {
            return Optional.of(jwtGenerator.generateToken(username));
        } catch (JOSEException e) {
            return Optional.empty();
        }
    }
}
