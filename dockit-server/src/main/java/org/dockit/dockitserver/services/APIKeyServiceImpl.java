package org.dockit.dockitserver.services;

import org.dockit.dockitserver.caching.CacheNames;
import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.repositories.APIKeyRepository;
import org.dockit.dockitserver.services.templates.APIKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = {CacheNames.API_KEY})
public class APIKeyServiceImpl implements APIKeyService {

    private final APIKeyRepository APIKeyRepository;

    @Autowired
    public APIKeyServiceImpl(APIKeyRepository APIKeyRepository) {
        this.APIKeyRepository = APIKeyRepository;
    }

    @Override
    @CachePut(key = "#token.id")
    public APIKey save(APIKey token) {
        return APIKeyRepository.save(token);
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(UUID id) {
        APIKeyRepository.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteAllById(List<UUID> ids) {
        APIKeyRepository.deleteAllById(ids);
    }

    @Override
    public List<APIKey> findAll() {
        return APIKeyRepository.findAll();
    }

    @Override
    public Optional<APIKey> findByAgentId(UUID agentId) {
        List<APIKey> tokens = APIKeyRepository.findAll();
        return tokens.stream()
                .filter(t -> t.getAgent().getId().toString().equals(agentId.toString()))
                .findFirst();
    }

    @Override
    public long count() {
        return APIKeyRepository.count();
    }
}
