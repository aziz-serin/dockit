package org.dockit.dockitserver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.dockit.dockitserver.entitylisteners.AuditEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity object representing Audits in the database
 */
@Entity
@EntityListeners(AuditEntityListener.class)
@Table(name = "audit")
public class Audit implements DTO {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @NotEmpty
    @Column(name = "vm_id")
    private String vmId;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    @NotEmpty
    private Agent agent;

    @NotEmpty
    @Column(name = "category")
    private String category;

    @NotNull
    @Column(name = "time_stamp")
    private LocalDateTime timeStamp;

    @Column(name = "data", columnDefinition = "TEXT")
    private String data;

    public UUID getId() {
        return id;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
