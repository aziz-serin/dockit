package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.Agent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentService {
    Agent save(Agent agent);
    Optional<Agent> updateAgentName(UUID id, String newAgentName);
    Optional<Agent> updateLastActiveTime(UUID id, LocalDateTime lastActiveTime);
    void deleteById(UUID id);
    Optional<Agent> findById(UUID id);
    List<Agent> findAll();
    List<Agent> findByAgentName(String agentName);
    List<Agent> findAllSortedByCreationDateAscending();
    List<Agent> findAllSortedByCreationDateDescending();
    List<Agent> findAllByCreationDateBetweenTwoDates(LocalDateTime intervalStart, LocalDateTime intervalEnd);
    List<Agent> findAllRecentlyActiveInGivenMinutes(long minutes);
    List<Agent> findAllRecentlyNotActiveInGivenMinutes(long minutes);
    long count();
}
