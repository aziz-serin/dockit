package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Agent;

import java.util.List;
import java.util.UUID;

public interface APIKeyService {
    APIKey save(APIKey token);
    void deleteById(UUID id);
    void deleteAllById(List<UUID> ids);
    List<APIKey> findAll();
    List<APIKey> findAllWithSameAgent(Agent agent);
    long count();
}
