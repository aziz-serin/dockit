package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.AccessToken;

import java.util.List;

public interface AccessTokenService {
    AccessToken save(AccessToken token);
    void deleteById(Long id);
    void deleteExpired();
    List<AccessToken> findAll();
    List<AccessToken> findAllAscendingByExpiryDate();
    List<AccessToken> findAllDescendingByExpiryDate();
    long count();
}
