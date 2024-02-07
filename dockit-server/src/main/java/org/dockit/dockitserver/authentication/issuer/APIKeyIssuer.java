package org.dockit.dockitserver.authentication.issuer;

import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.security.apikeys.APIKeyGenerator;
import org.dockit.dockitserver.services.templates.APIKeyService;
import org.dockit.dockitserver.services.templates.AdminService;
import org.dockit.dockitserver.services.templates.AgentService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Issuer class responsible for the generation of API keys
 */
@Component
public class APIKeyIssuer {
    private final APIKeyService apiKeyService;
    private final AgentService agentService;
    private final AdminService adminService;

    /**
     * @param apiKeyService {@link APIKeyService} to be injected
     * @param agentService {@link AgentService} to be injected
     * @param adminService {@link AdminService} to be injected
     */
    public APIKeyIssuer(APIKeyService apiKeyService, AgentService agentService, AdminService adminService) {
        this.apiKeyService = apiKeyService;
        this.agentService = agentService;
        this.adminService = adminService;
    }

    /**
     * @param username username for {@link Admin}
     * @param password password for {@link Admin}
     * @param agentId id field of {@link Agent}
     * @return {@link Optional} containing the {@link APIKey} if successful, empty if not
     */
    public Optional<String> issueKey(String username, String password, UUID agentId) {
        Optional<Admin> admin = adminService.findByUsername(username);
        if (admin.isEmpty()) {
            return Optional.empty();
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, admin.get().getPassword())) {
            return Optional.empty();
        }
        Optional<Agent> agent = agentService.findById(agentId);
        if (agent.isEmpty()) {
            return Optional.empty();
        }
        String generatedKey = APIKeyGenerator.generateApiKey();
        Optional<APIKey> apiKey = EntityCreator.createAPIKey(generatedKey, agent.get());
        if (apiKey.isPresent()) {
            apiKeyService.save(apiKey.get());
            return Optional.of(generatedKey);
        } else {
            return Optional.empty();
        }
    }
}
