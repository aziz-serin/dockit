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
        // TODO: Hash Token
        return APIKeyRepository.save(token);
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        APIKeyRepository.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteExpired() {
        List<APIKey> tokens = APIKeyRepository.findAll();
        List<Long> expiredTokenIds = tokens.stream()
                .filter(t -> t.getExpiryDate().isBefore(LocalDateTime.now()))
                .map(APIKey::getId)
                .toList();
        deleteAllById(expiredTokenIds);
    }

    private void deleteAllById(List<Long> ids) {
        APIKeyRepository.deleteAllById(ids);
    }

    @Override
    public List<APIKey> findAll() {
        return APIKeyRepository.findAll();
    }

    @Override
    public List<APIKey> findAllWithSameAgent(Agent agent) {
        List<APIKey> tokens = APIKeyRepository.findAll();
        return tokens.stream()
                .filter(t -> t.getAgent().getId().equals(agent.getId()))
                .toList();
    }

    @Override
    public List<APIKey> findAllAscendingByExpiryDate() {
        List<APIKey> tokens = APIKeyRepository.findAll();
        return tokens.stream()
                .sorted(Comparator.comparing(APIKey::getExpiryDate))
                .toList();
    }

    @Override
    public List<APIKey> findAllDescendingByExpiryDate() {
        List<APIKey> tokens = APIKeyRepository.findAll();
        List<APIKey> sortedTokens = new ArrayList<>(tokens.stream()
                .sorted(Comparator.comparing(APIKey::getExpiryDate))
                .toList());
        Collections.reverse(sortedTokens);
        return sortedTokens;
    }

    @Override
    public long count() {
        return APIKeyRepository.count();
    }
}
