package org.dockit.dockitserver.entities.utils;

import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;

import java.time.LocalDateTime;

public class EntityValidator {
    protected static boolean validAgent(String agentName, String password, LocalDateTime creationTime, LocalDateTime lastActiveTime) {
        return agentName != null && password != null && creationTime.isBefore(LocalDateTime.now())
                && lastActiveTime.isBefore(LocalDateTime.now());
    }

    protected static boolean validAdmin(String username, String password, Admin.Role privilege) {
        return username != null && password != null && privilege != null;
    }

    protected static boolean validAudit(String vmId, String category, LocalDateTime timeStamp, String data) {
        return vmId != null && category != null && timeStamp.isBefore(LocalDateTime.now()) && data != null;
    }

    protected static boolean validAPIKey(String token, Agent agent) {
        return token != null && agent != null;
    }
}
