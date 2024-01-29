package org.dockit.dockitserver.authentication.issuer;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.services.templates.AdminService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtIssuerTest {
    @Autowired
    JwtIssuer jwtIssuer;
    @Autowired
    AdminService adminService;

    static final String USERNAME = "username";
    static final String PASSWORD = "password";

    Admin admin;

    @BeforeAll
    public void setup() {
        admin = EntityCreator.createAdmin(USERNAME, PASSWORD, Admin.Role.SUPER).get();
        adminService.save(admin);
    }

    @Test
    public void issueJwtReturnsEmptyGivenInvalidUsername() {
        Optional<String> jwt = jwtIssuer.issueJwt("rrrrr", PASSWORD);

        assertTrue(jwt.isEmpty());
    }

    @Test
    public void issueJwtReturnsEmptyGivenInvalidPassword() {
        Optional<String> jwt = jwtIssuer.issueJwt(USERNAME, "passsword");

        assertTrue(jwt.isEmpty());
    }

    @Test
    public void issueJwtReturnsJwt() {
        Optional<String> jwt = jwtIssuer.issueJwt(USERNAME, PASSWORD);

        assertTrue(jwt.isPresent());
    }
}
