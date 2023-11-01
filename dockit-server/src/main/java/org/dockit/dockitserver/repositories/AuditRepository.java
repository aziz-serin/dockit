package org.dockit.dockitserver.repositories;

import org.dockit.dockitserver.entities.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<Audit, Long> {
}
