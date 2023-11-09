package org.dockit.dockitserver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "agents")
public class Agent implements DTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Size(max=512)
    @Column(name = "username")
    private String agentName;

    @NotEmpty
    @Column(name = "password")
    private String password;

    @NotEmpty
    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @NotEmpty
    @Column(name = "last_active_time")
    private LocalDateTime lastActiveTime;

    @NotEmpty
    @Column(name = "is_active")
    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(LocalDateTime lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }
}
