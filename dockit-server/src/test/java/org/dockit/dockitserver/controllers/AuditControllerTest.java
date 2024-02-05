package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.security.encryption.AESCBCEncryptor;
import org.dockit.dockitserver.security.key.KeyConstants;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.dockit.dockitserver.services.templates.AdminService;
import org.dockit.dockitserver.services.templates.AuditService;
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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuditControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    AuditService auditService;
    @Autowired
    AdminService adminService;
    @Autowired
    KeyStoreHandler keyStoreHandler;

    static final String VM_ID = "vmId";
    static final String CATEGORY = "category";
    static final String DATA = "data to encrypt";
    static final LocalDateTime TIME_STAMP = LocalDateTime.now();
    static final String ADMIN_USERNAME = "admin";
    static final String VIEWER_ADMIN_USERNAME = "viewer";
    static final String ADMIN_PASSWORD = "password";

    WebTestClient client;
    Audit audit;
    SecretKey key;

    @BeforeAll
    public void setup() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Admin admin = EntityCreator.createAdmin(ADMIN_USERNAME, ADMIN_PASSWORD, Admin.Role.SUPER).get();
        adminService.save(admin);

        Admin viewerAdmin = EntityCreator.createAdmin(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, Admin.Role.VIEWER).get();
        adminService.save(viewerAdmin);

        key = (SecretKey) keyStoreHandler.getKey(KeyConstants.DB_KEY_ALIAS, "".toCharArray()).get();

        createAuditAndSave();

        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void getAuditFailsGivenWrongId() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/audit?id=" + UUID.randomUUID())
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void getAuditSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/audit?id=" + audit.getId())
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Audit.class)
                .consumeWith(res -> assertThat(res.getResponseBody().getId().toString())
                        .isEqualTo(audit.getId().toString())
                );
    }

    @Test
    public void getByCategorySucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/audit/category?category=" + CATEGORY)
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res -> assertThat(res.getResponseBody())
                        .isNotEmpty()
                );
    }

    @Test
    public void getByVmIdSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/audit/vmId?vmId=" + VM_ID)
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res -> assertThat(res.getResponseBody())
                        .isNotEmpty()
                );
    }

    @Test
    public void getByCategoryVmIdSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/audit/categoryVmId?vmId=" + VM_ID + "&category=" + CATEGORY)
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res -> assertThat(res.getResponseBody())
                        .isNotEmpty()
                );
    }

    @Test
    public void getByCategorySortedSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/audit/sortedCategory?category=" + CATEGORY + "&isAscending=" + true)
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res -> assertThat(res.getResponseBody())
                        .isNotEmpty()
                );
    }

    @Test
    public void getByVmIdSortedSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/audit/sortedVmId?vmId=" + VM_ID + "&isAscending=" + false)
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res -> assertThat(res.getResponseBody())
                        .isNotEmpty()
                );
    }

    @Test
    public void getSortedSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/audit/sorted?isAscending=" + true)
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res -> assertThat(res.getResponseBody())
                        .isNotEmpty()
                );
    }

    @Test
    public void deleteAllByIdsFailGivenInsufficientPermissions() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.delete().uri("/api/audit/byIds?ids=", audit.getId())
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void deleteAllByIdsSucceeds() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.delete().uri("/api/audit/byIds?ids=", audit.getId())
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        //Create it again for other methods
        createAuditAndSave();
    }

    @Test
    public void deleteByIdFailsGivenInsufficientPermissions() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.delete().uri("/api/audit?id=", audit.getId())
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void deleteByIdSucceeds() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.delete().uri("/api/audit?id=" + audit.getId())
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        //Create it again for other methods
        createAuditAndSave();
    }

    private void createAuditAndSave() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String encryptedData = AESCBCEncryptor.encrypt(DATA, key);

        audit = EntityCreator.createAudit(VM_ID, CATEGORY, TIME_STAMP, encryptedData).get();
        auditService.save(audit);
    }
}
