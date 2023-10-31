package org.dockit.dockitserver.repositories;

import org.dockit.dockitserver.entities.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
}
