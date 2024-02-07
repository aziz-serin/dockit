package org.dockit.dockitserver.repositories;

import org.dockit.dockitserver.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository used to interact with {@link Admin}
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
}
