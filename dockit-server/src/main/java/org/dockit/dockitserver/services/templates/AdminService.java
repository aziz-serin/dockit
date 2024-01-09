package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    Admin save(Admin admin);
    Optional<Admin> updateUsername(Long id, String newUsername);
    Optional<Admin> updatePassword(Long id, String newPassword);
    Optional<Admin> updateRole(Long id, Admin.Role role);
    void deleteById(Long id);
    Optional<Admin> findById(Long id);
    Optional<Admin> findByUsername(String username);
    List<Admin> findAll();
    List<Admin> findAllById(List<Long> ids);
    List<Admin> findByRole(Admin.Role role);
    long count();
    boolean existsById(Long id);
}
