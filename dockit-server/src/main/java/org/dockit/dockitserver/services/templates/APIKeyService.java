package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Agent;

import java.util.List;

public interface APIKeyService {
    APIKey save(APIKey token);
    void deleteById(Long id);
    void deleteAllById(List<Long> ids);
    List<APIKey> findAll();
    List<APIKey> findAllWithSameAgent(Agent agent);
    long count();
}
