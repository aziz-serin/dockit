package org.dockit.dockitserver.repositories;

import org.dockit.dockitserver.entities.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Long> {
}
