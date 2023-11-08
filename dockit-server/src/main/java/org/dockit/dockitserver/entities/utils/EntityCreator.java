package org.dockit.dockitserver.entities.utils;

import org.dockit.dockitserver.entities.AccessToken;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.entities.RefreshToken;

import java.time.LocalDateTime;
import java.util.Optional;

public class EntityCreator {
    public static Optional<Agent> createAgent(String agentName, String password, LocalDateTime creationTime,
                                    LocalDateTime lastActiveTime, boolean isActive) {
        if (!EntityValidator.validAgent(agentName, password, creationTime, lastActiveTime)) {
            return Optional.empty();
        }
        Agent agent = new Agent();
        agent.setAgentName(agentName);
        agent.setPassword(password);
        agent.setCreationTime(creationTime);
        agent.setLastActiveTime(lastActiveTime);
        agent.setActive(isActive);
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

    public static Optional<AccessToken> createAccessToken(String token, LocalDateTime expiryTime) {
        if (!EntityValidator.validAccessToken(token, expiryTime)) {
            return Optional.empty();
        }
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(token);
        accessToken.setExpiryDate(expiryTime);
        return Optional.of(accessToken);
    }

    public static Optional<RefreshToken> createRefreshToken(String token, LocalDateTime expiryTime) {
        if (!EntityValidator.validRefreshToken(token, expiryTime)) {
            return Optional.empty();
        }
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(expiryTime);
        return Optional.of(refreshToken);
    }
}
