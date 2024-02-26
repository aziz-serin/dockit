package org.dockit.dockitserver.entities.utils;

import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Utility class to aid the creation of new entities
 */
public class EntityCreator {
    /**
     * Create a new agent entity
     *
     * @param agentName string specifying the name of the {@link Agent}
     * @param password string specifying the password of the {@link Agent}
     * @param creationTime time specifying the creation time of the {@link Agent}
     * @param lastActiveTime time specifying the last active time of the {@link Agent}
     * @param allowedUsers list of allowed users for the host of the {@link Agent}
     * @param agentUrl url to communicate with {@link Agent}
     * @return {@link Optional} agent if successful, empty if not
     */
    public static Optional<Agent> createAgent(String agentName, String password, LocalDateTime creationTime,
                                              LocalDateTime lastActiveTime, List<String> allowedUsers, URL agentUrl) {
        if (!EntityValidator.validAgent(agentName, password, creationTime, lastActiveTime, allowedUsers, agentUrl)) {
            return Optional.empty();
        }
        Agent agent = new Agent();
        agent.setAgentName(agentName);
        agent.setPassword(password);
        agent.setCreationTime(creationTime);
        agent.setLastActiveTime(lastActiveTime);
        agent.setAllowedUsers(allowedUsers);
        agent.setAgentUrl(agentUrl);
        return Optional.of(agent);
    }

    /**
     * Create a new admin entity
     *
     * @param username string specifying the username of the {@link Admin}
     * @param password string specifying the password of the {@link Admin}
     * @param privilege {@link Admin.Role} specifying the role of the {@link Admin}
     * @return {@link Optional} admin if successful, empty if not
     */
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

    /**
     * Create a new audit entity
     *
     * @param vmId string specifying the vmId of the {@link Audit}
     * @param category string specifying the category of the {@link Audit}
     * @param timeStamp time specifying the timeStamp of the {@link Audit}
     * @param data string specifying the data of the {@link Audit}
     * @param agent {@link Agent} sender of the {@link Audit}
     * @return {@link Optional} audit if successful, empty if not
     */
    public static Optional<Audit> createAudit(String vmId, String category, LocalDateTime timeStamp, String data,
                                              Agent agent) {
        if (!EntityValidator.validAudit(vmId, category, timeStamp, data, agent)) {
            return Optional.empty();
        }
        Audit audit = new Audit();
        audit.setVmId(vmId);
        audit.setAgent(agent);
        audit.setCategory(category);
        audit.setTimeStamp(timeStamp);
        audit.setData(data);

        return Optional.of(audit);
    }

    /**
     * Create a new apikey entity
     *
     * @param token string specifying the token for the {@link APIKey}
     * @param agent {@link Agent} owner of the key
     * @return {@link Optional} apikey if successful, empty if not
     */
    public static Optional<APIKey> createAPIKey(String token, Agent agent) {
        if (!EntityValidator.validAPIKey(token, agent)) {
            return Optional.empty();
        }
        APIKey APIKey = new APIKey();
        APIKey.setToken(token);
        APIKey.setAgent(agent);
        return Optional.of(APIKey);
    }

    /**
     * Create a new alert entity
     *
     * @param vmId string specifying the vmId of the {@link Alert}
     * @param agent {@link Agent} instance for the {@link Alert}
     * @param importance importance of the alert
     * @param auditTimeStamp time taken from the {@link Audit} objects timestamp
     * @param message string message about the description of the alert
     * @return {@link Optional} alert if successful, empty if not
     */
    public static Optional<Alert> createAlert(String vmId, Agent agent, Alert.Importance importance,
                                              LocalDateTime auditTimeStamp, String message) {
        if (!EntityValidator.validAlert(vmId, agent, importance, auditTimeStamp, message)) {
            return Optional.empty();
        }
        Alert alert = new Alert();
        alert.setAgent(agent);
        alert.setImportance(importance);
        alert.setVmId(vmId);
        alert.setAuditTimeStamp(auditTimeStamp);
        alert.setMessage(message);

        return Optional.of(alert);
    }
}
