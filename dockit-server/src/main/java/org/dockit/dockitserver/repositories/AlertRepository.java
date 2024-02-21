package org.dockit.dockitserver.repositories;

import org.dockit.dockitserver.entities.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository used to interact with {@link Alert}
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
}
