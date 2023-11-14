package org.dockit.dockitserver.services;

import org.dockit.dockitserver.caching.CacheNames;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.repositories.AuditRepository;
import org.dockit.dockitserver.services.templates.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {CacheNames.AUDIT})
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    @Autowired
    public AuditServiceImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    @CachePut(key = "#audit.id")
    public Audit save(Audit audit) {
        return auditRepository.save(audit);
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        auditRepository.deleteById(id);
    }

    @Override
    @Cacheable(key = "#id")
    public Optional<Audit> findById(Long id) {
        return auditRepository.findById(id);
    }

    @Override
    public List<Audit> findByVmId(String vmId) {
        return auditRepository.findAll().stream()
                .filter(a -> a.getVmId().equals(vmId))
                .toList();
    }

    @Override
    public List<Audit> findByCategory(String category) {
        return auditRepository.findAll().stream()
                .filter(a -> a.getCategory().equals(category))
                .toList();
    }

    @Override
    public List<Audit> findByCategoryAndVmId(String category, String vmId) {
        return auditRepository.findAll().stream()
                .filter(a -> a.getCategory().equals(category) && a.getVmId().equals(vmId))
                .toList();
    }

    @Override
    public List<Audit> findAllSortByTimeStampAscending() {
        return auditRepository.findAll().stream()
                .sorted(Comparator.comparing(Audit::getTimeStamp))
                .toList();
    }

    @Override
    public List<Audit> findAllSortByTimeStampDescending() {
        List<Audit> audits = new ArrayList<>(auditRepository.findAll().stream()
                .sorted(Comparator.comparing(Audit::getTimeStamp))
                .toList());
        Collections.reverse(audits);
        return audits;
    }

    @Override
    public List<Audit> findAllByCategorySortByTimeStampAscending(String category) {
        return auditRepository.findAll().stream()
                .filter(a -> a.getCategory().equals(category))
                .sorted(Comparator.comparing(Audit::getTimeStamp))
                .toList();
    }

    @Override
    public List<Audit> findAllByCategorySortByTimeStampDescending(String category) {
        List<Audit> audits = new ArrayList<>(auditRepository.findAll().stream()
                .filter(a -> a.getCategory().equals(category))
                .sorted(Comparator.comparing(Audit::getTimeStamp))
                .toList());
        Collections.reverse(audits);
        return audits;
    }

    @Override
    public List<Audit> findAllByVmIdSortByTimeStampAscending(String vmId) {
        return auditRepository.findAll().stream()
                .filter(a -> a.getVmId().equals(vmId))
                .sorted(Comparator.comparing(Audit::getTimeStamp))
                .toList();
    }

    @Override
    public List<Audit> findAllByVmIdSortByTimeStampDescending(String vmId) {
        List<Audit> audits = new ArrayList<>(auditRepository.findAll().stream()
                .filter(a -> a.getVmId().equals(vmId))
                .sorted(Comparator.comparing(Audit::getTimeStamp))
                .toList());
        Collections.reverse(audits);
        return audits;
    }

    @Override
    public List<Audit> findAllByVmIdInGivenInterval(String vmId, LocalDateTime startInterval, LocalDateTime endInterval) {
        return auditRepository.findAll()
                .stream()
                .filter(a -> a.getVmId().equals(vmId))
                .filter(a -> a.getTimeStamp().isAfter(startInterval) && a.getTimeStamp().isBefore(endInterval))
                .toList();
    }

    @Override
    public long count() {
        return auditRepository.count();
    }
}
