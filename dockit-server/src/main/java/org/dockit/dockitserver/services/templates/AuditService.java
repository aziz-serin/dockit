package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.Audit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuditService {
    Audit save(Audit audit);
    void deleteById(Long id);
    Optional<Audit> findById(Long id);
    List<Audit> findByVmId(String vmId);
    List<Audit> findByCategory(String category);
    List<Audit> findByCategoryAndVmId(String category, String vmId);
    List<Audit> findAllSortByTimeStampAscending();
    List<Audit> findAllSortByTimeStampDescending();
    List<Audit> findAllByCategorySortByTimeStampAscending(String category);
    List<Audit> findAllByCategorySortByTimeStampDescending(String category);
    List<Audit> findAllByVmIdSortByTimeStampAscending(String vmId);
    List<Audit> findAllByVmIdSortByTimeStampDescending(String vmId);
    List<Audit> findAllByVmIdInGivenInterval(String vmId, LocalDateTime startInterval, LocalDateTime endInterval);
    long count();
}
