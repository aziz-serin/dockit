package org.dockit.dockitserver.authentication.issuer;

import java.util.Optional;
import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.services.templates.APIKeyService;
import org.dockit.dockitserver.services.templates.AdminService;
import org.dockit.dockitserver.services.templates.AgentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class APIKeyIssuerTest {

    @Autowired
    APIKeyIssuer apiKeyIssuer;
    @Autowired
    AgentService agentService;
    @Autowired
    AdminService adminService;
    @Autowired
    APIKeyService apiKeyService;

    static final String USERNAME = "user_name";
    static final String PASSWORD = "password";

    Agent agent;
    Admin admin;

    @BeforeAll
    public void setup() {
        agent = EntityCreator.createAgent("agent1", "password1",
                LocalDateTime.now(), LocalDateTime.now(), true).get();
        agentService.save(agent);

        admin = EntityCreator.createAdmin(USERNAME, PASSWORD, Admin.Role.SUPER).get();
        adminService.save(admin);
    }

    @Test
    public void issueKeyReturnsEmptyGivenAdminDoesNotExists() {
        long countBefore = apiKeyService.count();
        Optional<APIKey> apiKey = apiKeyIssuer.issueKey("user", PASSWORD, agent.getId());

        assertTrue(apiKey.isEmpty());
        assertEquals(countBefore, apiKeyService.count());
    }

    @Test
    public void issueKeyReturnsEmptyGivenAdminPasswordInvalid() {
        long countBefore = apiKeyService.count();
        Optional<APIKey> apiKey = apiKeyIssuer.issueKey(USERNAME, "invalid_password", agent.getId());

        assertTrue(apiKey.isEmpty());
        assertEquals(countBefore, apiKeyService.count());
    }

    @Test
    public void issueKeyReturnsEmptyGivenAgentDoesNotExist() {
        long countBefore = apiKeyService.count();
        Optional<APIKey> apiKey = apiKeyIssuer.issueKey(USERNAME, PASSWORD, 999L);

        assertTrue(apiKey.isEmpty());
        assertEquals(countBefore, apiKeyService.count());
    }

    @Test
    public void issueKeyCreatesAndReturnsKey() {
        Optional<APIKey> apiKey = apiKeyIssuer.issueKey(USERNAME, PASSWORD, agent.getId());

        assertTrue(apiKey.isPresent());
        assertThat(apiKey.get().getAgent().getId()).isEqualTo(agent.getId());
        assertThat(apiKeyService.findAll().stream().map(APIKey::getId).toList()).contains(apiKey.get().getId());
    }

}
