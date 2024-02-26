package org.dockit.dockitserver.entities.utils;

import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.springframework.security.web.util.UrlUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
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
     * @param allowedUsers list of allowed users for the host of the {@link Agent}
     * @param agentUrl url to communicate with {@link Agent}
     * @return {@link Optional} agent if successful, empty if not
     */
    protected static boolean validAgent(String agentName, String password, LocalDateTime creationTime,
                                        LocalDateTime lastActiveTime, List<String> allowedUsers, URL agentUrl) {
        return agentName != null && password != null && creationTime.isBefore(LocalDateTime.now())
                && lastActiveTime.isBefore(LocalDateTime.now()) && agentName.length() < 255 && password.length() < 255
                && !allowedUsers.isEmpty() && agentUrl != null;
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
        return username != null && password != null && privilege != null &&
                username.length() < 255 && password.length() < 255;
    }

    /**
     * Validates details for the audit entity
     *
     * @param vmId string specifying the vmId of the {@link Audit}
     * @param category string specifying the category of the {@link Audit}
     * @param timeStamp time specifying the timeStamp of the {@link Audit}
     * @param data string specifying the data of the {@link Audit}
     * @param agent {@link Agent} sender of the {@link Audit}
     * @return {@link Optional} audit if successful, empty if not
     */
    protected static boolean validAudit(String vmId, String category, LocalDateTime timeStamp, String data,
                                        Agent agent) {
        // Check if any null, and check char limits for string fields
        return vmId != null && category != null && timeStamp.isBefore(LocalDateTime.now()) && data != null
                && agent != null && vmId.length() < 255 && category.length() < 255;
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

    /**
     * Validate attributes for a new alert entity
     *
     * @param vmId string specifying the vmId of the {@link Alert}
     * @param agent {@link Agent} instance for the {@link Alert}
     * @param importance importance of the alert
     * @param auditTimeStamp time taken from the {@link Audit} objects timestamp
     * @param message string message about the description of the alert
     * @return true if valid, false if not
     */
    protected static boolean validAlert(String vmId, Agent agent, Alert.Importance importance,
                                        LocalDateTime auditTimeStamp, String message) {
        return vmId != null && agent != null && importance != null && auditTimeStamp.isBefore(LocalDateTime.now())
                && message != null && vmId.length() < 255;
    }
}
