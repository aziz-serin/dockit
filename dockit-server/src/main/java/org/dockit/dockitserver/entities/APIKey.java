package org.dockit.dockitserver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

/**
 * Entity object representing APIKey in the database
 */
@Entity
@Table(name = "api_key")
public class APIKey implements DTO {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "agent")
    private Agent agent;

    @NotEmpty
    @Column(name = "token")
    private String token;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getToken() {
        return token;
    }

    /**
     * Hashes the token using {@link BCryptPasswordEncoder} then sets it as the token
     *
     * @param token token to be stored
     */
    public void setToken(String token) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.token = encoder.encode(token);
    }
}
