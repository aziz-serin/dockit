package org.dockit.dockitserver.repositories;

import org.dockit.dockitserver.entities.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository used to interact with {@link Agent}
 */
@Repository
public interface AgentRepository extends JpaRepository<Agent, UUID> {
}
