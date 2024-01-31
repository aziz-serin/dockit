package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.services.templates.AdminService;
import org.dockit.dockitserver.testUtils.TokenObtain;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    AdminService adminService;

    static final String SUPER_ADMIN_USERNAME = "superAdmin";
    static final String VIEWER_ADMIN_USERNAME = "viewerAdmin";
    static final String EDITOR_ADMIN_USERNAME = "editorAdmin";
    static final String PASSWORD = "password";

    Admin superAdmin;
    Admin viewerAdmin;
    Admin editorAdmin;

    private WebTestClient client;

    @BeforeAll
    public void setup() {
        superAdmin = EntityCreator.createAdmin(SUPER_ADMIN_USERNAME, PASSWORD, Admin.Role.SUPER).get();
        adminService.save(superAdmin);

        viewerAdmin = EntityCreator.createAdmin(VIEWER_ADMIN_USERNAME, PASSWORD, Admin.Role.VIEWER).get();
        adminService.save(viewerAdmin);

        editorAdmin = EntityCreator.createAdmin(EDITOR_ADMIN_USERNAME, PASSWORD, Admin.Role.EDITOR).get();
        adminService.save(editorAdmin);

        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void getAdminsReturnsAllAdmins() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);

        client.get().uri("/api/admin")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArrayList.class)
                .consumeWith(res -> {
                    assertThat(Objects.requireNonNull(res.getResponseBody()).size())
                            .isEqualTo(adminService.findAll().size());
                });
    }

    @Test
    public void updateUsernameFailsGivenInsufficientPermissions() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "username", viewerAdmin.getUsername(),
                "new_user_name", "new_user_name"
        );

        client.put().uri("/api/admin/updateUsername")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateUsernameFailsGivenInvalidBody() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "usernme", viewerAdmin.getUsername(),
                "new_user_name", "new_user_name"
        );

        client.put().uri("/api/admin/updateUsername")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateUsernameFailsGivenNonUniqueUserName() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "username", viewerAdmin.getUsername(),
                "new_user_name", superAdmin.getUsername()
        );

        client.put().uri("/api/admin/updateUsername")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateUsernameSucceeds() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "username", viewerAdmin.getUsername(),
                "new_user_name", "new_username"
        );

        client.put().uri("/api/admin/updateUsername")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().isOk();

        //Undo the operation
        Map<String, ?> jsonReverse = Map.of(
                "username", "new_username",
                "new_user_name", VIEWER_ADMIN_USERNAME
        );

        client.put().uri("/api/admin/updateUsername")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonReverse))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void updatePasswordFailsGivenInsufficientPermissions() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "username", viewerAdmin.getUsername(),
                "new_password", "new_password"
        );

        client.put().uri("/api/admin/updatePassword")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updatePasswordFailsGivenInvalidBody() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "usernme", viewerAdmin.getUsername(),
                "new_password", "new_password"
        );

        client.put().uri("/api/admin/updatePassword")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updatePasswordSucceeds() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "username", viewerAdmin.getUsername(),
                "new_password", PASSWORD
        );

        client.put().uri("/api/admin/updatePassword")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void updateRoleFailsGivenInsufficientPermissions() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "username", viewerAdmin.getUsername(),
                "new_role", "VIEWER"
        );

        client.put().uri("/api/admin/updateRole")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateRoleFailsGivenInvalidBody() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "username", viewerAdmin.getUsername(),
                "new_rle", "VIEWER"
        );

        client.put().uri("/api/admin/updateRole")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateRoleFailsGivenInvalidRole() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "username", viewerAdmin.getUsername(),
                "new_role", "VIEW"
        );

        client.put().uri("/api/admin/updateRole")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateRoleSucceeds() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "username", viewerAdmin.getUsername(),
                "new_role", "VIEWER"
        );

        client.put().uri("/api/admin/updateRole")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void createFailsGivenInsufficientPermissions() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "username", "new_admin",
                "password", PASSWORD,
                "role", "SUPER"
        );

        client.post().uri("/api/admin")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void createFailsGivenInvalidBody() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);

        Map<String, ?> json = Map.of(
                "username", "new_admin",
                "pasword", PASSWORD,
                "role", "SUPER"
        );

        client.post().uri("/api/admin")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void createSucceeds() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);
        String newUsername = "new_admin";
        Map<String, ?> json = Map.of(
                "username", newUsername,
                "password", PASSWORD,
                "role", "SUPER"
        );

        client.post().uri("/api/admin")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().isOk();

        assertTrue(adminService.findByUsername(newUsername).isPresent());
    }

    @Test
    public void deleteFailsGivenInsufficientPermission() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, PASSWORD, client);

        client.delete().uri("/api/admin?userName=" + editorAdmin.getUsername())
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void deleteSucceeds() {
        String jwt = TokenObtain.getJwt(SUPER_ADMIN_USERNAME, PASSWORD, client);

        client.delete().uri("/api/admin?userName=" + EDITOR_ADMIN_USERNAME)
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        assertThat(adminService.findByUsername(EDITOR_ADMIN_USERNAME)).isEmpty();
    }
}
