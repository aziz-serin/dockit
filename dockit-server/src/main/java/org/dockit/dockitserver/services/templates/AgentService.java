package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.Agent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer to interact with the Agent object
 */
public interface AgentService {
    /**
     * Save the agent object
     *
     * @param agent {@link Agent} to be saved
     * @return saved {@link Agent}
     */
    Agent save(Agent agent);

    /**
     * Update the agent username
     *
     * @param id id of the {@link Agent} to be updated
     * @param newAgentName new name for the agent
     * @return {@link Optional} Agent if agent exists, empty if not
     */
    Optional<Agent> updateAgentName(UUID id, String newAgentName);

    /**
     * Update the agent lastActiveTime
     *
     * @param id id of the {@link Agent} to be updated
     * @param lastActiveTime {@link LocalDateTime} object representing time
     * @return {@link Optional} Agent if agent exists, empty if not
     */
    Optional<Agent> updateLastActiveTime(UUID id, LocalDateTime lastActiveTime);

    /**
     * Delete agent using its id
     *
     * @param id of the {@link Agent} to be deleted
     */
    void deleteById(UUID id);

    /**
     * Find agent using its id
     *
     * @param id id of the {@link Agent} to be found
     * @return {@link Optional} Agent if agent exists, empty if not
     */
    Optional<Agent> findById(UUID id);

    /**
     * Find all agents
     *
     * @return List of all the agents from the database
     */
    List<Agent> findAll();

    /**
     * Find all agents with the same name
     *
     * @param agentName name for an {@link Agent}
     * @return List of all agents with the same name
     */
    List<Agent> findByAgentName(String agentName);

    /**
     * Find all agents sorted by their creation date in ascending order
     *
     * @return List of agents
     */
    List<Agent> findAllSortedByCreationDateAscending();

    /**
     * Find all agents sorted by their creation date in descending order
     *
     * @return List of agents
     */
    List<Agent> findAllSortedByCreationDateDescending();

    /**
     * Find all agents by their creation date in a given interval
     *
     * @param intervalStart {@link LocalDateTime} object representing time
     * @param intervalEnd {@link LocalDateTime} object representing time
     * @return List of agents
     */
    List<Agent> findAllByCreationDateBetweenTwoDates(LocalDateTime intervalStart, LocalDateTime intervalEnd);

    /**
     * Find all agents active in the last specified minutes
     *
     * @param minutes representing time
     * @return List of agents
     */
    List<Agent> findAllRecentlyActiveInGivenMinutes(long minutes);

    /**
     * Find all agents not active in the last specified minutes
     *
     * @param minutes representing time
     * @return List of agents
     */
    List<Agent> findAllRecentlyNotActiveInGivenMinutes(long minutes);

    /**
     * @return counts of agents
     */
    long count();
}
