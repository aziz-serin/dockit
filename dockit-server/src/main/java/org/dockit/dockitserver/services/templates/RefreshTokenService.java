package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.RefreshToken;

import java.util.List;

public interface RefreshTokenService {
    void deleteById(Long id);
    void deleteExpired();
    List<RefreshToken> findAll();
    List<RefreshToken> findAllAscendingByExpiryDate();
    List<RefreshToken> findAllDescendingByExpiryDate();
    long count();
}
