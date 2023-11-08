package org.dockit.dockitserver.entities.utils;

import org.dockit.dockitserver.entities.AccessToken;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.entities.RefreshToken;

import java.time.LocalDateTime;

public class EntityCreator {
    public static Agent createAgent(String agentName, String password, LocalDateTime creationTime,
                                    LocalDateTime lastActiveTime, boolean isActive) {
        Agent agent = new Agent();
        agent.setAgentName(agentName);
        agent.setPassword(password);
        agent.setCreationTime(creationTime);
        agent.setLastActiveTime(lastActiveTime);
        agent.setActive(isActive);
        return agent;
    }

    public static Admin createAdmin(String username, String password, Admin.Role privilege) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setPrivilege(privilege);
        return admin;
    }

    public static Audit createAudit(String vmId, String category, LocalDateTime timeStamp, String data) {
        Audit audit = new Audit();
        audit.setVmId(vmId);
        audit.setCategory(category);
        audit.setTimeStamp(timeStamp);
        audit.setData(data);
        return audit;
    }

    public static AccessToken createAccessToken(String token, LocalDateTime expiryTime) {
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(token);
        accessToken.setExpiryDate(expiryTime);
        return accessToken;
    }

    public static RefreshToken createRefreshToken(String token, LocalDateTime expiryTime) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(expiryTime);
        return refreshToken;
    }
}
