package org.dockit.dockitserver.services;

import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.repositories.AdminRepository;
import org.dockit.dockitserver.services.templates.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Admin save(Admin admin) {
        // TODO encrypt username and password fields
        return adminRepository.save(admin);
    }

    @Override
    public Optional<Admin> updateUsername(Long id, String newUsername) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            admin.setUsername(newUsername);
            return Optional.of(adminRepository.save(admin));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Admin> updatePassword(Long id, String newPassword) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            admin.setPassword(newPassword);
            return Optional.of(adminRepository.save(admin));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Admin> updateRole(Long id, Admin.Role role) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            admin.setPrivilege(role);
            return Optional.of(adminRepository.save(admin));
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        adminRepository.deleteById(id);
    }

    @Override
    public Optional<Admin> findById(Long id) {
        return adminRepository.findById(id);
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public List<Admin> findAllById(List<Long> ids) {
        return adminRepository.findAllById(ids);
    }

    @Override
    public List<Admin> findByRole(Admin.Role role) {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream()
                .filter(admin -> admin.getPrivilege().equals(role))
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return adminRepository.count();
    }

    @Override
    public boolean existsById(Long id) {
        return adminRepository.existsById(id);
    }
}
