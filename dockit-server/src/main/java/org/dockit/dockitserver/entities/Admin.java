package org.dockit.dockitserver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name= "admins")
public class Admin implements DTO {

    public enum Role {
        SUPER,
        EDITOR,
        VIEWER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 512)
    @NotEmpty
    @Column(name = "username")
    private String username;

    @NotEmpty
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "privilege")
    private Role privilege;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Role privilege) {
        this.privilege = privilege;
    }
}
