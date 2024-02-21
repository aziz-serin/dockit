package org.dockit.dockitserver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity object representing alerts in the database
 */
@Entity
public class Alert implements DTO {
    /**
     * Enum containing importance indicator for Alerts
     */
    public enum Importance {
        CRITICAL,
        MEDIUM,
        LOW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @NotEmpty
    @Column(name = "vmId")
    private String vmId;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    @NotEmpty
    private Agent agent;

    @NotEmpty
    @Column(name = "importance")
    private Importance importance;

    @NotEmpty
    @Column(name = "audit_timestamp")
    private LocalDateTime auditTimeStamp;

    @NotEmpty
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Importance getImportance() {
        return importance;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }

    public LocalDateTime getAuditTimeStamp() {
        return auditTimeStamp;
    }

    public void setAuditTimeStamp(LocalDateTime auditTimeStamp) {
        this.auditTimeStamp = auditTimeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
