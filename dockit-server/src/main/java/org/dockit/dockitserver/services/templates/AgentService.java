package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.Agent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AgentService {
    Agent save(Agent agent);
    Optional<Agent> updateAgentName(Long id, String newAgentName);
    Optional<Agent> updateLastActiveTime(Long id, LocalDateTime lastActiveTime);
    void deleteById(Long id);
    Optional<Agent> findById(Long id);
    List<Agent> findAll();
    List<Agent> findByAgentName(String agentName);
    List<Agent> findAllSortedByCreationDateAscending();
    List<Agent> findAllSortedByCreationDateDescending();
    List<Agent> findAllByCreationDateBetweenTwoDates(LocalDateTime intervalStart, LocalDateTime intervalEnd);
    List<Agent> findAllRecentlyActiveInGivenMinutes(long minutes);
    List<Agent> findAllRecentlyNotActiveInGivenMinutes(long minutes);
    long count();
}
