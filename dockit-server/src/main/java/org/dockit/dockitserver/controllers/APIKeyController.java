package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.services.templates.APIKeyService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

/**
 * Controller containing the endpoints for apikey operations
 */
@RestController
@RequestMapping(path = "/api/apikey", produces = {MediaType.APPLICATION_JSON_VALUE})
public class APIKeyController {

    private final APIKeyService apiKeyService;

    /**
     * @param apiKeyService {@link APIKeyService} to be injected
     */
    public APIKeyController(APIKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    /**
     * Deletes the apiKey of an agent by using agent's id
     *
     * @param agentId agentId specified to find the apikey
     * @return Response entity containing the response
     */
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> revoke(@RequestParam(name="agentId") @NonNull String agentId) {
        Optional<APIKey> apiKey = apiKeyService.findByAgentId(UUID.fromString(agentId));
        if (apiKey.isPresent()) {
            apiKeyService.deleteById(apiKey.get().getId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * An endpoint to check if an apiKey exists for the given agent
     *
     * @param agentId agentId specified to find the apikey
     * @return Response entity containing the response
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> get(@RequestParam(name="agentId") @NonNull String agentId) {
        Optional<APIKey> apiKey = apiKeyService.findByAgentId(UUID.fromString(agentId));
        if (apiKey.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
