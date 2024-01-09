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

@Component
public class APIKeyIssuer {
    private final APIKeyService apiKeyService;
    private final AgentService agentService;
    private final AdminService adminService;

    public APIKeyIssuer(APIKeyService apiKeyService, AgentService agentService, AdminService adminService) {
        this.apiKeyService = apiKeyService;
        this.agentService = agentService;
        this.adminService = adminService;
    }
    public Optional<APIKey> issueKey(String username, String password, Long agentId) {
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
        Optional<APIKey> apiKey = EntityCreator.createAPIKey(APIKeyGenerator.generateApiKey(), agent.get());
        if (apiKey.isPresent()) {
            apiKeyService.save(apiKey.get());
            return apiKey;
        } else {
            return Optional.empty();
        }
    }
}