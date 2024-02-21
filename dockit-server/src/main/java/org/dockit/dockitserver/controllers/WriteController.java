package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.controllers.utils.ParameterValidator;
import org.dockit.dockitserver.controllers.utils.WriteProcessor;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.services.templates.AgentService;
import org.dockit.dockitserver.services.templates.AuditService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller class containing the endpoints for write operations for {@link Audit}
 */
@RestController
@RequestMapping(path = "/api/write", produces = {MediaType.APPLICATION_JSON_VALUE})
public class WriteController {
    private final AuditService auditService;
    private final AgentService agentService;
    private final WriteProcessor writeProcessor;

    /**
     * @param auditService {@link AuditService} object to be injected
     * @param writeProcessor {@link WriteProcessor} object to be injected
     * @param agentService {@link AgentService} object to be injected
     */
    public WriteController(AuditService auditService, WriteProcessor writeProcessor, AgentService agentService) {
        this.auditService = auditService;
        this.agentService = agentService;
        this.writeProcessor = writeProcessor;
    }

    /**
     * Creates a new {@link Audit} entry
     *
     * @param body should contain the parameters: <br>
     *             "vmId" -> vmId string for the audit <br>
     *             "category" -> category string for the audit <br>
     *             "timeStamp" -> timeStamps string in
     *             <a href="https://www.iso.org/iso-8601-date-and-time-format.html">ISO Local Date Time format</a> <br>
     * @param id id of an {@link Agent} which sent the request
     * @return Response entity containing the response
     */
    @PostMapping
    public ResponseEntity<?> write(@RequestBody @NonNull Map<String, ?> body, @RequestParam(name = "id") UUID id) {
        // Here check if the request contains required parameters, decrypt the data, create audit entity using it,
        // encrypt it using db key, then save it
        String vmId = (String) body.get("vmId");
        String category = (String) body.get("category");
        Optional<LocalDateTime> timeStamp = parseTimeStamp((String) body.get("timeStamp"));
        String data = (String) body.get("data");
        if (ParameterValidator.invalid(vmId, category, data)) {
            return ResponseEntity.badRequest().body("Invalid Request!");
        }
        if (timeStamp.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid time format! Should comply with ISO format!");
        }
        Optional<Agent> agent = agentService.findById(id);
        if (agent.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid Request!");
        }

        Optional<String> dataToWrite = writeProcessor.process(agent.get(), data);

        if (dataToWrite.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        Optional<Audit> audit = EntityCreator.createAudit(vmId, category, timeStamp.get(), dataToWrite.get(),
                agent.get());
        if (audit.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }

        auditService.save(audit.get());
        return ResponseEntity.ok().build();
    }

    private Optional<LocalDateTime> parseTimeStamp(String time) {
        try {
            LocalDateTime timeStamp = LocalDateTime.parse(time);
            return Optional.of(timeStamp);
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
