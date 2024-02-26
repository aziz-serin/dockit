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

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller containing the endpoints for agent operations
 */
@RestController
@RequestMapping(path = "/api/agent", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AgentController {
    private final AgentService agentService;
    private final APIKeyService apiKeyService;
    private final KeyHandler keyHandler;
    private final KeyStoreHandler keyStoreHandler;

    /**
     * @param agentService {@link AgentService} to be injected
     * @param apiKeyService {@link APIKeyService} to be injected
     * @param keyHandler {@link KeyHandler} to be injected
     * @param keyStoreHandler {@link KeyStoreHandler} to be injected
     */
    public AgentController(AgentService agentService, APIKeyService apiKeyService, KeyHandler keyHandler,
                           KeyStoreHandler keyStoreHandler) {
        this.agentService = agentService;
        this.apiKeyService = apiKeyService;
        this.keyHandler = keyHandler;
        this.keyStoreHandler = keyStoreHandler;
    }

    /**
     * Return specified agent from their id
     *
     * @param id id of the agent to be returned
     * @return Response entity containing the response
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getAgent(@RequestParam(name = "id") UUID id) {
        Optional<Agent> agent = agentService.findById(id);
        if (agent.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(agent.get());
        }
    }

    /**
     * Returns all agents from the response
     *
     * @return Response entity containing the response
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getAgents() {
        List<Agent> agents = agentService.findAll();
        return ResponseEntity.ok(agents);
    }

    /**
     * Returns all agents by their creation date in the specified sorted order
     *
     * @param isAscending specifies the sorting order of the response
     * @return Response entity containing the response
     */
    @GetMapping("/creationDate")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getAgentsByCreationDate(@RequestParam(name = "isAscending") boolean isAscending) {
        if (isAscending) {
            return ResponseEntity.ok().body(agentService.findAllSortedByCreationDateAscending());
        } else {
            return ResponseEntity.ok().body(agentService.findAllSortedByCreationDateDescending());
        }
    }

    /**
     * Returns all agents active in given minutes
     *
     * @param minutes an integer value representing the interval in minutes
     * @return Response entity containing the response
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getAgentsLastActiveInGivenMinutes(@RequestParam(name = "time") int minutes) {
        return ResponseEntity.ok().body(agentService.findAllRecentlyActiveInGivenMinutes(minutes));
    }

    /**
     * Creates a new agent and saves it in the database with its secret key
     *
     * @param body Should contain parameters: <br>
     *             "agentName" -> name of the agent to be created <br>
     *             "password" -> password for the new agent <br>
     *             "allowedUsers" -> list of allowed users seperated by space in the form of string <br>
     *             "agentUrl" -> url to be used to communicate with the agent <br>
     * @return Response entity containing the response
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> creteAgent(@RequestBody @NonNull Map<String, ?> body) {
        String agentName = (String) body.get("agentName");
        String password = (String) body.get("password");
        String allowedUsers = (String) body.get("allowedUsers");
        String agentUrl = (String) body.get("agentUrl");
        if (ParameterValidator.invalid(agentName, password, allowedUsers, agentUrl)) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime lastActiveTime = LocalDateTime.now();
        List<String> listAllowedUsers = Arrays.stream(allowedUsers.split(" ")).toList();
        URL url;
        try {
            url = new URL(agentUrl);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        Optional<Agent> agent = EntityCreator.createAgent(agentName, password, creationTime, lastActiveTime,
                listAllowedUsers, url);

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

    /**
     * Delete an agent using their id
     *
     * @param id id of the agent to be deleted
     * @return Response entity containing the response
     */
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> delete(@RequestParam(name = "id") UUID id) {
        // Delete agent and any associated apikey with it, as well as its secret key
        Optional<Agent> agent = agentService.findById(id);
        if (agent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Optional<APIKey> apiKey = apiKeyService.findByAgentId(agent.get().getId());

        apiKey.ifPresent(key -> apiKeyService.deleteById(key.getId()));
        agentService.deleteById(id);
        keyStoreHandler.deleteKey(String.valueOf(id));
        return ResponseEntity.ok().build();
    }

    /**
     * Update the username of a given agent
     *
     * @param id id of the agent to be updated
     * @param body Should contain the parameters: <br>
     *             "agentName" -> new name for the specified agent <br>
     * @return Response entity containing the response
     */
    @PutMapping("/name")
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

    /**
     * Update the allowedUsers list of a given agent
     *
     * @param id id of the agent to be updated
     * @param body Should contain the parameters: <br>
     *             "allowedUsers" -> list of allowed users seperated by space in the form of string <br>
     * @return Response entity containing the response
     */
    @PutMapping("/allowedUsers")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> updateAllowedUsers(@RequestParam(name = "id") UUID id,
                                             @RequestBody @NonNull Map<String, ?> body) {
        String allowedUsers = (String) body.get("allowedUsers");
        if (ParameterValidator.invalid(allowedUsers)) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        List<String> allowedUserList = new ArrayList<>(List.of(allowedUsers.split(" ")));

        Optional<Agent> agent = agentService.updateAllowedUsers(id, allowedUserList);
        if (agent.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Update the agentUrl
     *
     * @param id id of the agent to be updated
     * @param body Should contain the parameters: <br>
     *             "agentUrl" -> new url for the agent <br>
     * @return Response entity containing the response
     */
    @PutMapping("/agentUrl")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> updateAgentUrl(@RequestParam(name = "id") UUID id,
                                                @RequestBody @NonNull Map<String, ?> body) {
        String agentUrl = (String) body.get("agentUrl");
        if (ParameterValidator.invalid(agentUrl)) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        URL url;
        try {
            url = new URL(agentUrl);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }

        Optional<Agent> agent = agentService.updateAgentUrl(id, url);
        if (agent.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        return ResponseEntity.ok().build();
    }
}
