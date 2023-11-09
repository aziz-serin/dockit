package org.dockit.dockitserver.caching;

import org.dockit.dockitserver.entities.DTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class CacheManagerWrapper {
    private final CacheManager cacheManager;

    @Autowired
    public CacheManagerWrapper(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void evictAllCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    public <T> void evictFromCache(String cacheName, T key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    public <T> void evictGivenKeysFromCache(String cacheName, List<T> keys) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            for(T key : keys) {
                cache.evict(key);
            }
        }
    }

    public <T> Optional<Object> getFromCache(String cacheName, T key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            return Optional.ofNullable(Objects.requireNonNull(cache.get(key)).get());
        }
        return Optional.empty();
    }

    public <T> void insertIntoCache(String cacheName, T key, DTO value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.putIfAbsent(key, value);
        }
    }

    public <T> void insertIntoCacheMultipleValues(String cacheName, List<DTO> values) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            for (DTO value : values) {
                cache.putIfAbsent(value.getId(), value);
            }
        }
    }
}
