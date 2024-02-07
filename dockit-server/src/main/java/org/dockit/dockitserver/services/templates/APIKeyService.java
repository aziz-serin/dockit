package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Agent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer to interact with the APIKey object
 */
public interface APIKeyService {

    /**
     * Save the apikey object
     *
     * @param token api key to be saved
     * @return saved {@link APIKey}
     */
    APIKey save(APIKey token);

    /**
     * Delete specified key
     *
     * @param id {@link APIKey} to be deleted
     */
    void deleteById(UUID id);

    /**
     * Find all the api keys from the database
     *
     * @return list of api keys
     */
    List<APIKey> findAll();

    /**
     * Find the apikey for the specified agent
     *
     * @param agentId id for the {@link Agent}
     * @return {@link Optional} empty if not found, {@link APIKey} if found
     */
    Optional<APIKey> findByAgentId(UUID agentId);

    /**
     * @return count of api keys
     */
    long count();
}
