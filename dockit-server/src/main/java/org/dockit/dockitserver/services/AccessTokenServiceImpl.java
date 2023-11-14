package org.dockit.dockitserver.services;

import org.dockit.dockitserver.caching.CacheNames;
import org.dockit.dockitserver.entities.AccessToken;
import org.dockit.dockitserver.repositories.AccessTokenRepository;
import org.dockit.dockitserver.services.templates.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@CacheConfig(cacheNames = {CacheNames.ACCESS_TOKEN})
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;

    @Autowired
    public AccessTokenServiceImpl(AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository;
    }

    @Override
    @CachePut(key = "#token.id")
    public AccessToken save(AccessToken token) {
        // TODO: Encrypt Token
        return accessTokenRepository.save(token);
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        accessTokenRepository.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteExpired() {
        List<AccessToken> tokens = accessTokenRepository.findAll();
        List<Long> expiredTokenIds = tokens.stream()
                .filter(t -> t.getExpiryDate().isBefore(LocalDateTime.now()))
                .map(AccessToken::getId)
                .toList();
        deleteAllById(expiredTokenIds);
    }

    private void deleteAllById(List<Long> ids) {
        accessTokenRepository.deleteAllById(ids);
    }

    @Override
    public List<AccessToken> findAll() {
        return accessTokenRepository.findAll();
    }

    @Override
    public List<AccessToken> findAllAscendingByExpiryDate() {
        List<AccessToken> tokens = accessTokenRepository.findAll();
        return tokens.stream()
                .sorted(Comparator.comparing(AccessToken::getExpiryDate))
                .toList();
    }

    @Override
    public List<AccessToken> findAllDescendingByExpiryDate() {
        List<AccessToken> tokens = accessTokenRepository.findAll();
        List<AccessToken> sortedTokens = new ArrayList<>(tokens.stream()
                .sorted(Comparator.comparing(AccessToken::getExpiryDate))
                .toList());
        Collections.reverse(sortedTokens);
        return sortedTokens;
    }

    @Override
    public long count() {
        return accessTokenRepository.count();
    }
}
