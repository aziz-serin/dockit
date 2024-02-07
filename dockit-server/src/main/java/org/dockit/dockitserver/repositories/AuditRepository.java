package org.dockit.dockitserver.repositories;

import org.dockit.dockitserver.entities.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository used to interact with {@link Audit}
 */
@Repository
public interface AuditRepository extends JpaRepository<Audit, UUID> {
}
