package org.dockit.dockitserver.services;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.caching.CacheNames;
import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.services.templates.APIKeyService;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.services.templates.AgentService;
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
public class APIKeyServiceTest {

    private static final String CACHE_NAME = CacheNames.API_KEY;

    @Autowired
    private APIKeyService APIKeyService;
    @Autowired
    private AgentService agentService;

    @Autowired
    private CacheManager cacheManager;

    private APIKey APIKey1;
    private APIKey APIKey2;
    private APIKey APIKey3;
    private Agent agent;

    @BeforeAll
    public void setup() {
        agent = EntityCreator.createAgent("agent1", "password1",
                LocalDateTime.now(), LocalDateTime.now(), true).get();
        agentService.save(agent);

        APIKey1 = EntityCreator.createAPIKey("token1", LocalDateTime.now().plusHours(1), agent).get();
        APIKeyService.save(APIKey1);

        APIKey2 = new APIKey();
        APIKey2.setToken("token2");
        APIKey2.setExpiryDate(LocalDateTime.now().minusMinutes(5));
        APIKey2.setAgent(agent);
        APIKeyService.save(APIKey2);

        APIKey3 = EntityCreator.createAPIKey("token3", LocalDateTime.now().plusDays(1), agent).get();
        APIKeyService.save(APIKey3);
    }

    @AfterEach
    public void clearCache() {
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME)).clear();
    }

    @Test
    public void findAllReturnsAllTokens() {
        assertThat(APIKeyService.findAll()).hasSize(3);
    }

    @Test
    public void findAllAscendingByExpiryDateReturnsAscendingList() {
        List<APIKey> tokens = APIKeyService.findAllAscendingByExpiryDate();

        assertThat(tokens).hasSize(3);
        assertThat(tokens.get(0).getId()).isEqualTo(APIKey2.getId());
        assertThat(tokens.get(1).getId()).isEqualTo(APIKey1.getId());
        assertThat(tokens.get(2).getId()).isEqualTo(APIKey3.getId());
    }

    @Test
    public void findAllByAgentReturnsTrueList() {
        List<APIKey> tokens = APIKeyService.findAllWithSameAgent(agent);
        tokens.forEach(
                t -> assertThat(t.getAgent().getId()).isEqualTo(agent.getId())
        );
    }

    @Test
    public void findAllDescendingByExpiryDateReturnsDescendingList() {
        List<APIKey> tokens = APIKeyService.findAllDescendingByExpiryDate();

        assertThat(tokens).hasSize(3);
        assertThat(tokens.get(0).getId()).isEqualTo(APIKey3.getId());
        assertThat(tokens.get(1).getId()).isEqualTo(APIKey1.getId());
        assertThat(tokens.get(2).getId()).isEqualTo(APIKey2.getId());
    }

    @Test
    public void deleteExpiredDeletesAllExpiredTokens() {
        APIKeyService.deleteExpired();

        List<APIKey> tokens = APIKeyService.findAll();
        assertThat(tokens).hasSize(2);
        tokens.forEach(token ->
                assertThat(token.getExpiryDate()).isAfter(LocalDateTime.now()));

        // Undo the effects of this test
        APIKeyService.save(APIKey2);
    }

    @Test
    public void saveCachesTheResultOfTheOperation() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        APIKey tempToken = EntityCreator.createAPIKey("token", LocalDateTime.now().plusDays(1), agent).get();
        APIKeyService.save(tempToken);

        assertThat(cache).isNotNull();
        APIKey cachedToken = (APIKey) Objects.requireNonNull(cache.get(tempToken.getId())).get();

        assertThat(cachedToken).isNotNull();
        assertThat(cachedToken.getId()).isEqualTo(tempToken.getId());

        //Undo the effects of this test
        APIKeyService.deleteById(tempToken.getId());
    }

    @Test
    public void deleteByIdEvictsOnlyDeletedItemFromCache() {
        Cache cache = cacheManager.getCache(CACHE_NAME);

        APIKey tempToken1 = EntityCreator.createAPIKey("token", LocalDateTime.now().plusDays(1), agent).get();
        APIKey tempToken2 = EntityCreator.createAPIKey("token", LocalDateTime.now().plusDays(1), agent).get();
        APIKeyService.save(tempToken1);
        APIKeyService.save(tempToken2);

        assertThat(cache).isNotNull();

        APIKeyService.deleteById(tempToken1.getId());

        Object cachedToken1 = cache.get(tempToken1.getId());
        assertThat(cachedToken1).isNull();

        APIKey cachedToken2 = (APIKey) Objects.requireNonNull(cache.get(tempToken2.getId())).get();

        assertThat(cachedToken2).isNotNull();
        assertThat(cachedToken2.getId()).isEqualTo(tempToken2.getId());

        //Undo the effects of this test
        APIKeyService.deleteById(tempToken2.getId());
    }

    @Test
    public void deleteExpiredEvictsAllCache() {
        Cache cache = cacheManager.getCache(CACHE_NAME);

        APIKey tempToken = EntityCreator.createAPIKey("token", LocalDateTime.now().plusDays(1), agent).get();
        APIKeyService.save(tempToken);

        assertThat(cache).isNotNull();

        APIKeyService.deleteExpired();

        Object cachedToken = cache.get(tempToken.getId());
        assertThat(cachedToken).isNull();


        //Undo the effects of this test
        APIKeyService.deleteById(tempToken.getId());
        APIKeyService.save(APIKey2);
    }
}
