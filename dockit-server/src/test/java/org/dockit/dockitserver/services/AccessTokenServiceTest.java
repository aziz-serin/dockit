package org.dockit.dockitserver.services;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.caching.CacheNames;
import org.dockit.dockitserver.entities.AccessToken;
import org.dockit.dockitserver.services.templates.AccessTokenService;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccessTokenServiceTest {

    private static final String CACHE_NAME = CacheNames.ACCESS_TOKEN;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private CacheManager cacheManager;

    private AccessToken accessToken1;
    private AccessToken accessToken2;
    private AccessToken accessToken3;

    @BeforeAll
    public void setup() {
        accessToken1 = EntityCreator.createAccessToken("token1", LocalDateTime.now().plusHours(1)).get();
        accessTokenService.save(accessToken1);

        accessToken2 = new AccessToken();
        accessToken2.setToken("token2");
        accessToken2.setExpiryDate(LocalDateTime.now().minusMinutes(5));
        accessTokenService.save(accessToken2);

        accessToken3 = EntityCreator.createAccessToken("token3", LocalDateTime.now().plusDays(1)).get();
        accessTokenService.save(accessToken3);
    }

    @AfterEach
    public void clearCache() {
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME)).clear();
    }

    @Test
    public void findAllReturnsAllTokens() {
        assertThat(accessTokenService.findAll()).hasSize(3);
    }

    @Test
    public void findAllAscendingByExpiryDateReturnsAscendingList() {
        List<AccessToken> tokens = accessTokenService.findAllAscendingByExpiryDate();

        assertThat(tokens).hasSize(3);
        assertThat(tokens.get(0).getId()).isEqualTo(accessToken2.getId());
        assertThat(tokens.get(1).getId()).isEqualTo(accessToken1.getId());
        assertThat(tokens.get(2).getId()).isEqualTo(accessToken3.getId());
    }

    @Test
    public void findAllDescendingByExpiryDateReturnsDescendingList() {
        List<AccessToken> tokens = accessTokenService.findAllDescendingByExpiryDate();

        assertThat(tokens).hasSize(3);
        assertThat(tokens.get(0).getId()).isEqualTo(accessToken3.getId());
        assertThat(tokens.get(1).getId()).isEqualTo(accessToken1.getId());
        assertThat(tokens.get(2).getId()).isEqualTo(accessToken2.getId());
    }

    @Test
    public void deleteExpiredDeletesAllExpiredTokens() {
        accessTokenService.deleteExpired();

        List<AccessToken> tokens = accessTokenService.findAll();
        assertThat(tokens).hasSize(2);
        tokens.forEach(token ->
                assertThat(token.getExpiryDate()).isAfter(LocalDateTime.now()));

        // Undo the effects of this test
        accessTokenService.save(accessToken2);
    }

    @Test
    public void saveCachesTheResultOfTheOperation() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        AccessToken tempToken = EntityCreator.createAccessToken("token", LocalDateTime.now().plusDays(1)).get();
        accessTokenService.save(tempToken);

        assertThat(cache).isNotNull();
        AccessToken cachedToken = (AccessToken) Objects.requireNonNull(cache.get(tempToken.getId())).get();

        assertThat(cachedToken).isNotNull();
        assertThat(cachedToken.getId()).isEqualTo(tempToken.getId());

        //Undo the effects of this test
        accessTokenService.deleteById(tempToken.getId());
    }

    @Test
    public void deleteByIdEvictsOnlyDeletedItemFromCache() {
        Cache cache = cacheManager.getCache(CACHE_NAME);

        AccessToken tempToken1 = EntityCreator.createAccessToken("token", LocalDateTime.now().plusDays(1)).get();
        AccessToken tempToken2 = EntityCreator.createAccessToken("token", LocalDateTime.now().plusDays(1)).get();
        accessTokenService.save(tempToken1);
        accessTokenService.save(tempToken2);

        assertThat(cache).isNotNull();

        accessTokenService.deleteById(tempToken1.getId());

        Object cachedToken1 = cache.get(tempToken1.getId());
        assertThat(cachedToken1).isNull();

        AccessToken cachedToken2 = (AccessToken) Objects.requireNonNull(cache.get(tempToken2.getId())).get();

        assertThat(cachedToken2).isNotNull();
        assertThat(cachedToken2.getId()).isEqualTo(tempToken2.getId());

        //Undo the effects of this test
        accessTokenService.deleteById(tempToken2.getId());
    }

    @Test
    public void deleteExpiredEvictsAllCache() {
        Cache cache = cacheManager.getCache(CACHE_NAME);

        AccessToken tempToken = EntityCreator.createAccessToken("token", LocalDateTime.now().plusDays(1)).get();
        accessTokenService.save(tempToken);

        assertThat(cache).isNotNull();

        accessTokenService.deleteExpired();

        Object cachedToken = cache.get(tempToken.getId());
        assertThat(cachedToken).isNull();


        //Undo the effects of this test
        accessTokenService.deleteById(tempToken.getId());
        accessTokenService.save(accessToken2);
    }
}
