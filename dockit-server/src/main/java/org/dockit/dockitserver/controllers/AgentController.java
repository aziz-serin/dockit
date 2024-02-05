package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.controllers.utils.ParameterValidator;
import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.exceptions.security.key.KeyStoreException;
import org.dockit.dockitserver.security.key.KeyHandler;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.dockit.dockitserver.services.templates.APIKeyService;
import org.dockit.dockitserver.services.templates.AgentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/agent", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AgentController {
    private final AgentService agentService;
    private final APIKeyService apiKeyService;
    private final KeyHandler keyHandler;
    private final KeyStoreHandler keyStoreHandler;

    public AgentController(AgentService agentService, APIKeyService apiKeyService, KeyHandler keyHandler,
                           KeyStoreHandler keyStoreHandler) {
        this.agentService = agentService;
        this.apiKeyService = apiKeyService;
        this.keyHandler = keyHandler;
        this.keyStoreHandler = keyStoreHandler;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getAgent(@RequestParam(name = "id") UUID id) {
        Optional<Agent> agent = agentService.findById(id);
        if (agent.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        } else {
            return ResponseEntity.ok().body(agent.get());
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getAgents() {
        List<Agent> agents = agentService.findAll();
        return ResponseEntity.ok(agents);
    }

    @GetMapping("/creationDate")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getAgentsByCreationDate(@RequestParam(name = "isAscending") boolean isAscending) {
        if (isAscending) {
            return ResponseEntity.ok().body(agentService.findAllSortedByCreationDateAscending());
        } else {
            return ResponseEntity.ok().body(agentService.findAllSortedByCreationDateDescending());
        }
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getAgentsLastActiveInGivenMinutes(@RequestParam(name = "time") int minutes) {
        return ResponseEntity.ok().body(agentService.findAllRecentlyActiveInGivenMinutes(minutes));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> creteAgent(@RequestBody @NonNull Map<String, ?> body) {
        String agentName = (String) body.get("agentName");
        String password = (String) body.get("password");
        if (ParameterValidator.invalid(agentName, password)) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime lastActiveTime = LocalDateTime.now();
        Optional<Agent> agent = EntityCreator.createAgent(agentName, password, creationTime, lastActiveTime);

        if (agent.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        agentService.save(agent.get());

        try {
            // Generate secret key for the agent and store it
            Key key = keyHandler.generateKeyForAgentAndSave(agent.get().getId().toString(), agent.get().getPassword());
            return ResponseEntity.ok().body(Map.of(
                    "id", agent.get().getId(),
                    "key", Base64.getEncoder().encodeToString(key.getEncoded()))
            );
        } catch (KeyStoreException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> delete(@RequestParam(name = "id") UUID id) {
        // Delete agent and any associated apikey with it, as well as its secret key
        Optional<Agent> agent = agentService.findById(id);
        if (agent.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        Optional<APIKey> apiKey = apiKeyService.findByAgentId(agent.get().getId());

        apiKey.ifPresent(key -> apiKeyService.deleteById(key.getId()));
        agentService.deleteById(id);
        keyStoreHandler.deleteKey(String.valueOf(id));
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> updateAgentName(@RequestParam(name = "id") UUID id,
                                                    @RequestBody @NonNull Map<String, ?> body) {
        String name = (String) body.get("agentName");
        if (ParameterValidator.invalid(name)) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        Optional<Agent> agent = agentService.updateAgentName(id, name);
        if (agent.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        return ResponseEntity.ok().build();
    }
}
