package org.dockit.dockitserver.entities.utils;

import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Audit;

import java.time.LocalDateTime;
import java.util.Optional;

public class EntityCreator {
    public static Optional<Agent> createAgent(String agentName, String password, LocalDateTime creationTime,
                                    LocalDateTime lastActiveTime) {
        if (!EntityValidator.validAgent(agentName, password, creationTime, lastActiveTime)) {
            return Optional.empty();
        }
        Agent agent = new Agent();
        agent.setAgentName(agentName);
        agent.setPassword(password);
        agent.setCreationTime(creationTime);
        agent.setLastActiveTime(lastActiveTime);
        return Optional.of(agent);
    }

    public static Optional<Admin> createAdmin(String username, String password, Admin.Role privilege) {
        if (!EntityValidator.validAdmin(username, password, privilege)) {
            return Optional.empty();
        }
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setPrivilege(privilege);
        return Optional.of(admin);
    }

    public static Optional<Audit> createAudit(String vmId, String category, LocalDateTime timeStamp, String data) {
        if (!EntityValidator.validAudit(vmId, category, timeStamp, data)) {
            return Optional.empty();
        }
        Audit audit = new Audit();
        audit.setVmId(vmId);
        audit.setCategory(category);
        audit.setTimeStamp(timeStamp);
        audit.setData(data);
        return Optional.of(audit);
    }

    public static Optional<APIKey> createAPIKey(String token, Agent agent) {
        if (!EntityValidator.validAPIKey(token, agent)) {
            return Optional.empty();
        }
        APIKey APIKey = new APIKey();
        APIKey.setToken(token);
        APIKey.setAgent(agent);
        return Optional.of(APIKey);
    }
}
