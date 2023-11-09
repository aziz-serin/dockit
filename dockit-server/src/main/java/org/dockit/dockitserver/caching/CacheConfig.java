package org.dockit.dockitserver.caching;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache(CacheNames.AUDIT,
                buildCache(200,1000, 1));

        cacheManager.registerCustomCache(CacheNames.AGENT,
                buildCache(15,50, 5));

        cacheManager.registerCustomCache(CacheNames.ADMIN,
                buildCache(15,50, 5));

        cacheManager.registerCustomCache(CacheNames.ACCESS_TOKEN,
                buildCache(100,500, 1));
        return cacheManager;
    }

    private <K, V> Cache<K, V> buildCache(
            int initialCapacity, int maximumSize, int durationInHours) {
        return Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterAccess(durationInHours, TimeUnit.HOURS)
                .recordStats()
                .build();
    }
}
