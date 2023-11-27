package org.dockit.dockitserver.caching;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.config.ConfigContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CacheConfigTest {

    @Autowired
    private CacheConfig cacheConfig;
    @Autowired
    private ConfigContainer configContainer;

    @Test
    public void cachesAreInitialisedProperly() {
        CacheManager cacheManager = cacheConfig.cacheManager(configContainer);
        assertTrue(cacheManager instanceof CaffeineCacheManager);
        assertThat(cacheManager.getCacheNames()).hasSize(4);
        assertThat(cacheManager.getCacheNames()).containsAll(List.of(
                CacheNames.AUDIT,
                CacheNames.API_KEY,
                CacheNames.AGENT,
                CacheNames.ADMIN
        ));
    }
}
