package org.dockit.dockitserver.entities.utils;

import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Audit;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Aid the creation of entities in {@link EntityCreator} class
 */
public class EntityValidator {
    /**
     * Validates details for the agent entity
     *
     * @param agentName string specifying the name of the {@link Agent}
     * @param password string specifying the password of the {@link Agent}
     * @param creationTime time specifying the creation time of the {@link Agent}
     * @param lastActiveTime time specifying the last active time of the {@link Agent}
     * @return {@link Optional} agent if successful, empty if not
     */
    protected static boolean validAgent(String agentName, String password, LocalDateTime creationTime, LocalDateTime lastActiveTime) {
        return agentName != null && password != null && creationTime.isBefore(LocalDateTime.now())
                && lastActiveTime.isBefore(LocalDateTime.now());
    }

    /**
     * Validates details for the admin entity
     *
     * @param username string specifying the username of the {@link Admin}
     * @param password string specifying the password of the {@link Admin}
     * @param privilege {@link Admin.Role} specifying the role of the {@link Admin}
     * @return {@link Optional} admin if successful, empty if not
     */
    protected static boolean validAdmin(String username, String password, Admin.Role privilege) {
        return username != null && password != null && privilege != null;
    }

    /**
     * Validates details for the audit entity
     *
     * @param vmId string specifying the vmId of the {@link Audit}
     * @param category string specifying the category of the {@link Audit}
     * @param timeStamp time specifying the timeStamp of the {@link Audit}
     * @param data string specifying the data of the {@link Audit}
     * @return {@link Optional} audit if successful, empty if not
     */
    protected static boolean validAudit(String vmId, String category, LocalDateTime timeStamp, String data) {
        return vmId != null && category != null && timeStamp.isBefore(LocalDateTime.now()) && data != null;
    }

    /**
     * Validates details for the apikey entity
     *
     * @param token string specifying the token for the {@link APIKey}
     * @param agent {@link Agent} owner of the key
     * @return {@link Optional} apikey if successful, empty if not
     */
    protected static boolean validAPIKey(String token, Agent agent) {
        return token != null && agent != null;
    }
}
