package org.dockit.dockitserver.services;

import org.dockit.dockitserver.caching.CacheNames;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.repositories.AdminRepository;
import org.dockit.dockitserver.services.templates.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {CacheNames.ADMIN})
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    @CachePut(key = "#admin.id")
    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    @CachePut(key = "#id")
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
    @CachePut(key = "#id")
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
    @CachePut(key = "#id")
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
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        adminRepository.deleteById(id);
    }

    @Override
    @Cacheable(key = "#id")
    public Optional<Admin> findById(Long id) {
        return adminRepository.findById(id);
    }

    @Override
    public Optional<Admin> findByUsername(String username) {
        List<Admin> admins = adminRepository.findAll();
        List<Admin> filteredAdmins = admins.stream()
                .filter(admin -> admin.getUsername().equals(username))
                .toList();
        if (filteredAdmins.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(filteredAdmins.get(0));
        }
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
