package org.dockit.dockitserver.repositories;

import org.dockit.dockitserver.entities.APIKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface APIKeyRepository extends JpaRepository<APIKey, Long> {
}
