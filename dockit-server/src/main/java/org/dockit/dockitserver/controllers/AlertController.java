package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.services.templates.AgentService;
import org.dockit.dockitserver.services.templates.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.dockit.dockitserver.entities.utils.AlertImportanceConverter.getImportance;

/**
 * Controller containing the endpoints for {@link Alert} operations
 */
@RestController
@RequestMapping(path = "/api/alert", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AlertController {
    private final AlertService alertService;
    private final AgentService agentService;

    /**
     * Constructor method for the controller
     *
     * @param alertService {@link AlertService} instance to be injected
     * @param agentService {@link AgentService} instance to be injected
     */
    @Autowired
    public AlertController(AlertService alertService, AgentService agentService) {
        this.alertService = alertService;
        this.agentService = agentService;
    }

    /**
     * Returns all alerts with matching vmId
     *
     * @param vmId has to be included as a request parameter to this endpoint
     * @return List of alerts if found any
     */
    @GetMapping("/vmId")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getWithSameVmId(@RequestParam(name="vmId") @NonNull String vmId) {
        List<Alert> alerts = alertService.findByVmId(vmId);
        return ResponseEntity.ok().body(alerts);
    }

    /**
     * Returns all alerts with matching agent
     *
     * @param agentId has to be included as a request parameter to this endpoint
     * @return List of alerts if found any, not found if agent does not exist, or bad request if
     * the agentId cannot be parsed
     */
    @GetMapping("/agentId")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getWithSameAgent(@RequestParam(name="id") @NonNull String agentId) {
        try {
            Optional<Agent> agent = agentService.findById(UUID.fromString(agentId));
            if (agent.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<Alert> alerts = alertService.findByAgent(agent.get());
            return ResponseEntity.ok().body(alerts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Return all alerts with matching importance. Importance should be one of the following:
     * ["CRITICAL", "MEDIUM", "LOW"]
     *
     * @param importance has to be included as a request parameter to this endpoint
     * @return List of alerts matching the importance, bad request if importance cannot be parsed
     */
    @GetMapping("/importance")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getWithSameImportance(@RequestParam(name="importance") @NonNull String importance) {
        Optional<Alert.Importance> parsedImportance = getImportance(importance);
        if (parsedImportance.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        List<Alert> alerts = alertService.findByImportance(parsedImportance.get());
        return ResponseEntity.ok().body(alerts);
    }

    /**
     * Return all alerts with matching importance for given vmId. Importance should be one of the following:
     * ["CRITICAL", "MEDIUM", "LOW"]
     *
     * @param importance has to be included as a request parameter to this endpoint
     * @param vmId has to be included as a request parameter to this endpoint
     * @return List of alerts matching the importance and the vmId, bad request if importance cannot be parsed
     */
    @GetMapping("/importanceVmId")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getWithSameImportanceWithSameVmId(@RequestParam(name="importance") @NonNull String importance,
                                                               @RequestParam(name="vmId") @NonNull String vmId) {
        Optional<Alert.Importance> parsedImportance = getImportance(importance);
        if (parsedImportance.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        List<Alert> alerts = alertService.findByImportanceWithSameVmId(parsedImportance.get(), vmId);
        return ResponseEntity.ok().body(alerts);
    }

    /**
     * Return all alerts with the same vmId sorted in descending order, with specified count
     *
     * @param count has to be included as a request parameter, and MUST be larger than or equal to 1
     * @param vmId has to be included as a parameter to this endpoint
     * @return List of alerts with specified count sorted with the most recent alerts for a given vmId
     */
    @GetMapping("/recentVmId")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getMostRecentWithVmId(@RequestParam(name="count") @NonNull int count,
                                                               @RequestParam(name="vmId") @NonNull String vmId) {
        if (count < 1) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        List<Alert> alerts = alertService.findMostRecentWithVmId(vmId, count);
        return ResponseEntity.ok().body(alerts);
    }

    /**
     * For the given alert id, delete the alert
     *
     * @param alertId id of the {@link Alert} to be deleted
     * @return bad request if alertId cannot be parsed, OK if alert is deleted
     */
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('SUPER')")
    public ResponseEntity<?> delete(@RequestParam(name="id") @NonNull String alertId) {
        try {
            UUID id = UUID.fromString(alertId);
            alertService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
