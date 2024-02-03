package org.dockit.dockitserver.services;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.caching.CacheNames;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.services.templates.AgentService;
import org.dockit.dockitserver.entities.utils.EntityCreator;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AgentServiceTest {
    private static final String CACHE_NAME = CacheNames.AGENT;

    @Autowired
    private AgentService agentService;

    @Autowired
    private CacheManager cacheManager;

    private Cache cache;
    private Agent agent1;
    private Agent agent2;
    private Agent agent3;

    @BeforeAll
    public void setup() {
        agent1 = EntityCreator.createAgent("agent1", "password1",
                LocalDateTime.now(), LocalDateTime.now()).get();
        agentService.save(agent1);

        agent2 = EntityCreator.createAgent("agent2", "password2",
                LocalDateTime.now().minusWeeks(1), LocalDateTime.now().minusDays(3)).get();
        agentService.save(agent2);

        agent3 = EntityCreator.createAgent("agent3", "password3",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusMinutes(5)).get();
        agentService.save(agent3);

        cache = Objects.requireNonNull(cacheManager.getCache(CACHE_NAME));
    }

    @Test
    public void findByIdReturnsEmptyIfIdDoesNotExist() {
        assertFalse(agentService.findById(UUID.randomUUID()).isPresent());
    }

    @Test
    public void findByIdReturnsAgent() {
        Optional<Agent> optionalAgent = agentService.findById(agent1.getId());

        assertTrue(optionalAgent.isPresent());
        assertThat(optionalAgent.get().getId()).isEqualTo(agent1.getId());
    }

    @Test
    public void countReturnsTrueCount() {
        assertThat(agentService.count()).isEqualTo(3);
    }

    @Test
    public void findAllReturnsAll() {
        assertThat(agentService.findAll()).hasSize(3);
    }

    @Test
    public void findByAgentNameReturnsAllAgentsWithSameName() {
        List<Agent> agents = agentService.findByAgentName("agent1");

        assertThat(agents).hasSize(1);
        assertThat(agents.get(0).getAgentName()).isEqualTo(agent1.getAgentName());
    }

    @Test
    public void findByAgentNameReturnsEmptyIfNameDoesNotExist() {
        List<Agent> agents = agentService.findByAgentName("TEST");

        assertThat(agents).hasSize(0);
    }

    @Test
    public void findAllSortedByCreationDateAscendingReturnsDatesInAscendingOrder() {
        List<Agent> agents = agentService.findAllSortedByCreationDateAscending();

        assertThat(agents).hasSize(3);
        assertThat(agents.get(0).getId()).isEqualTo(agent2.getId());
        assertThat(agents.get(1).getId()).isEqualTo(agent3.getId());
        assertThat(agents.get(2).getId()).isEqualTo(agent1.getId());
    }

    @Test
    public void findAllSortedByCreationDateDescendingReturnsDatesInDescendingOrder() {
        List<Agent> agents = agentService.findAllSortedByCreationDateDescending();

        assertThat(agents).hasSize(3);
        assertThat(agents.get(0).getId()).isEqualTo(agent1.getId());
        assertThat(agents.get(1).getId()).isEqualTo(agent3.getId());
        assertThat(agents.get(2).getId()).isEqualTo(agent2.getId());
    }

    @Test
    public void findAllByCreationDateBetweenTwoDatesReturnsTrueAgents() {
        LocalDateTime startInterval = LocalDateTime.now().minusDays(5);
        LocalDateTime endInterval = LocalDateTime.now().plusDays(1);

        List<Agent> agents = agentService.findAllByCreationDateBetweenTwoDates(startInterval, endInterval);
        assertThat(agents).hasSize(2);
        assertThat(agents.get(0).getCreationTime()).isBetween(startInterval, endInterval);
        assertThat(agents.get(1).getCreationTime()).isBetween(startInterval, endInterval);
    }

    @Test
    public void findAllRecentlyActiveInGivenMinutesReturnsTrueAgents() {
        long minutes = 15;
        List<Agent> agents = agentService.findAllRecentlyActiveInGivenMinutes(15);

        assertThat(agents).hasSize(2);
        assertThat(agents.get(0).getLastActiveTime())
                .isBetween(LocalDateTime.now().minusMinutes(minutes), LocalDateTime.now());
        assertThat(agents.get(1).getLastActiveTime())
                .isBetween(LocalDateTime.now().minusMinutes(minutes), LocalDateTime.now());
    }

    @Test
    public void findAllRecentlyNotActiveInGivenMinutesReturnsTrueAgents() {
        long minutes = 15;
        List<Agent> agents = agentService.findAllRecentlyNotActiveInGivenMinutes(minutes);

        assertThat(agents).hasSize(1);
        assertThat(agents.get(0).getLastActiveTime())
                .isBefore(LocalDateTime.now().minusMinutes(minutes));
    }

    @Test
    public void updateAgentNameReturnsEmptyIfIdDoesNotExist() {
        assertFalse(agentService.updateAgentName(UUID.randomUUID(), "TEST").isPresent());
    }

    @Test
    public void updateAgentNameUpdatesAgent() {
        Optional<Agent> agent = agentService.updateAgentName(agent2.getId(), "updatedAgent2");

        assertTrue(agent.isPresent());
        assertThat(agent.get().getAgentName()).isEqualTo("updatedAgent2");
        assertThat(agentService.findByAgentName("updatedAgent2")).hasSize(1);
    }

    @Test
    public void updateLastActiveTimeReturnsEmptyIfIdDoesNotExist() {
        assertFalse(agentService.updateLastActiveTime(UUID.randomUUID(), LocalDateTime.now()).isPresent());
    }

    @Test
    public void updateLastActiveTimeUpdatesAgent() {
        LocalDateTime beforeUpdate = agent2.getLastActiveTime();
        LocalDateTime updateValue = LocalDateTime.now();
        Optional<Agent> agent = agentService.updateLastActiveTime(agent2.getId(), updateValue);

        assertTrue(agent.isPresent());
        assertThat(agent.get().getLastActiveTime()).isEqualTo(updateValue);

        // Undo the update
        agentService.updateLastActiveTime(agent2.getId(), beforeUpdate);
    }

    @Test
    public void saveCachesResult() {
        Agent tempAgent = EntityCreator.createAgent("tempAgent", "password1",
                LocalDateTime.now(), LocalDateTime.now()).get();
        agentService.save(tempAgent);

        Agent cachedAgent = (Agent) cache.get(tempAgent.getId()).get();

        assertThat(Objects.requireNonNull(cachedAgent).getId()).isEqualTo(tempAgent.getId());

        //Undo the effects
        agentService.deleteById(tempAgent.getId());
    }

    @Test
    public void updateAgentNameUpdatesCachedValue() {
        Agent tempAgent = EntityCreator.createAgent("tempAgent", "password1",
                LocalDateTime.now(), LocalDateTime.now()).get();
        agentService.save(tempAgent);

        String newValue = "newUserName";
        agentService.updateAgentName(tempAgent.getId(), newValue);

        Agent cachedAgent = (Agent) cache.get(tempAgent.getId()).get();

        assertThat(Objects.requireNonNull(cachedAgent).getId()).isEqualTo(tempAgent.getId());
        assertThat(Objects.requireNonNull(cachedAgent).getAgentName()).isEqualTo(newValue);

        //Undo the effects
        agentService.deleteById(tempAgent.getId());
    }

    @Test
    public void updateLastActiveTimeUpdatesCachedValue() {
        Agent tempAgent = EntityCreator.createAgent("tempAgent", "password1",
                LocalDateTime.now(), LocalDateTime.now().minusMinutes(15)).get();
        agentService.save(tempAgent);

        LocalDateTime newTime = LocalDateTime.now();
        agentService.updateLastActiveTime(tempAgent.getId(), newTime);

        Agent cachedAgent = (Agent) cache.get(tempAgent.getId()).get();

        assertThat(Objects.requireNonNull(cachedAgent).getId()).isEqualTo(tempAgent.getId());
        assertThat(Objects.requireNonNull(cachedAgent).getLastActiveTime()).isEqualTo(newTime);

        //Undo the effects
        agentService.deleteById(tempAgent.getId());
    }

    @Test
    public void deleteEvictsDeletedValue() {
        Agent tempAgent = EntityCreator.createAgent("tempAgent", "password1",
                LocalDateTime.now(), LocalDateTime.now().minusMinutes(15)).get();
        agentService.save(tempAgent);

        agentService.deleteById(tempAgent.getId());
        Object cachedAgent = cache.get(tempAgent.getId());

        assertThat(cachedAgent).isNull();
    }

    @Test
    public void findByCachesSuccessfulFind() {
        // Place agent1 into cache
        agentService.findById(agent1.getId());

        Agent cachedAgent = (Agent) cache.get(agent1.getId()).get();

        assertThat(Objects.requireNonNull(cachedAgent).getId()).isEqualTo(agent1.getId());
    }

    @Test
    public void findByDoesNotCacheFailedFind() {
        UUID id = UUID.randomUUID();
        // Place agent1 into cache
        agentService.findById(id);

        Object cachedAgent = cache.get(id).get();

        assertThat(cachedAgent).isNull();
    }
}