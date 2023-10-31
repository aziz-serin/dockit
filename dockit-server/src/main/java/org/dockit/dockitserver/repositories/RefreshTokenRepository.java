package org.dockit.dockitserver.repositories;

import org.dockit.dockitserver.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
