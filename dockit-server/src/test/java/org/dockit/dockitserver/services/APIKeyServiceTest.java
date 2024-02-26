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

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class APIKeyServiceTest {
    private static final String CACHE_NAME = CacheNames.API_KEY;
    static final String DUMMY_URL_STRING = "http://someurl.com";

    @Autowired
    private APIKeyService APIKeyService;
    @Autowired
    private AgentService agentService;

    @Autowired
    private CacheManager cacheManager;

    private Agent agent1;
    private URL url;

    @BeforeAll
    public void setup() throws MalformedURLException {
        url = new URL(DUMMY_URL_STRING);
        agent1 = EntityCreator.createAgent("agent1", "password1",
                LocalDateTime.now(), LocalDateTime.now(), List.of(""), url).get();
        agentService.save(agent1);

        Agent agent2 = EntityCreator.createAgent("agent2", "password2",
                LocalDateTime.now(), LocalDateTime.now(), List.of(""), url).get();
        agentService.save(agent2);

        Agent agent3 = EntityCreator.createAgent("agent3", "password3",
                LocalDateTime.now(), LocalDateTime.now(), List.of(""), url).get();
        agentService.save(agent3);

        APIKey APIKey1 = EntityCreator.createAPIKey("token1", agent1).get();
        APIKeyService.save(APIKey1);

        APIKey APIKey2 = EntityCreator.createAPIKey("token2", agent2).get();
        APIKeyService.save(APIKey2);

        APIKey APIKey3 = EntityCreator.createAPIKey("token3", agent3).get();
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
    public void findAllByAgentReturnsToken() {
        Optional<APIKey> token = APIKeyService.findByAgentId(agent1.getId());

        assertThat(token).isPresent();
        assertThat(token.get().getAgent().getId()).isEqualTo(agent1.getId());
    }

    @Test
    public void saveCachesTheResultOfTheOperation() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        Agent tempAgent = EntityCreator.createAgent("tempAgent", "tempAgent",
                LocalDateTime.now(), LocalDateTime.now(), List.of(""), url).get();
        agentService.save(tempAgent);

        APIKey tempToken = EntityCreator.createAPIKey("token", tempAgent).get();
        APIKeyService.save(tempToken);

        assertThat(cache).isNotNull();
        APIKey cachedToken = (APIKey) Objects.requireNonNull(cache.get(tempToken.getId())).get();

        assertThat(cachedToken).isNotNull();
        assertThat(cachedToken.getId()).isEqualTo(tempToken.getId());

        //Undo the effects of this test
        APIKeyService.deleteById(tempToken.getId());
        agentService.deleteById(tempAgent.getId());
    }

    @Test
    public void deleteByIdEvictsOnlyDeletedItemFromCache() {
        Cache cache = cacheManager.getCache(CACHE_NAME);

        Agent tempAgent1 = EntityCreator.createAgent("tempAgent1", "tempAgent1",
                LocalDateTime.now(), LocalDateTime.now(), List.of(""), url).get();
        agentService.save(tempAgent1);
        Agent tempAgent2 = EntityCreator.createAgent("tempAgent2", "tempAgent2",
                LocalDateTime.now(), LocalDateTime.now(), List.of(""), url).get();
        agentService.save(tempAgent2);

        APIKey tempToken1 = EntityCreator.createAPIKey("token", tempAgent1).get();
        APIKey tempToken2 = EntityCreator.createAPIKey("token", tempAgent2).get();
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
        agentService.deleteById(tempAgent1.getId());
        agentService.deleteById(tempAgent2.getId());
    }
}
