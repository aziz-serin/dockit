package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.Admin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminService {
    Admin save(Admin admin);
    Optional<Admin> updateUsername(UUID id, String newUsername);
    Optional<Admin> updatePassword(UUID id, String newPassword);
    Optional<Admin> updateRole(UUID id, Admin.Role role);
    void deleteById(UUID id);
    Optional<Admin> findById(UUID id);
    Optional<Admin> findByUsername(String username);
    List<Admin> findAll();
    List<Admin> findAllById(List<UUID> ids);
    List<Admin> findByRole(Admin.Role role);
    long count();
    boolean existsById(UUID id);
}
