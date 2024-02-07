package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Agent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface APIKeyService {
    APIKey save(APIKey token);
    void deleteById(UUID id);
    List<APIKey> findAll();
    Optional<APIKey> findByAgentId(UUID agentId);
    long count();
}
