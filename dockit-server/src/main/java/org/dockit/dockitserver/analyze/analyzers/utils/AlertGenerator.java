package org.dockit.dockitserver.analyze.analyzers.utils;

import org.dockit.dockitserver.analyze.AuditCategories;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Generator utility class for alerts
 */
@Component
public class AlertGenerator {

    /**
     * Generate alert for resource-usage related data
     *
     * @param audit {@link Audit} to generate the {@link Alert} for
     * @param importance importance level for the alert
     * @param message message about the alert
     * @return {@link Optional} empty if alert can be created, empty if not
     */
    public Optional<Alert> generateAlert(Audit audit, Alert.Importance importance, String message) {
        String formattedMessage = getMessage(audit.getCategory(), audit.getVmId(), message, importance.toString());

        return EntityCreator.createAlert(audit.getVmId(), audit.getAgent(),
                importance, audit.getTimeStamp(), formattedMessage);
    }

    /**
     * Generate alert for user login related data
     *
     * @param audit {@link Audit} to generate the {@link Alert} for
     * @param message message about the alert
     * @return {@link Optional} empty if alert can be created, empty if not
     */
    public Optional<Alert> generateIntrusionAlert(Audit audit, String message) {

        return EntityCreator.createAlert(audit.getVmId(), audit.getAgent(),
                Alert.Importance.CRITICAL, audit.getTimeStamp(), message);
    }

    private String getMessage(String category, String vmId, String message, String importance) {
        switch (category) {
            case AuditCategories.DOCKER_CONTAINER_RESOURCE -> {
                String messageToFormat = AlertMessageTemplates.DOCKER_CONTAINER_RESOURCE_MESSAGE;
                return messageToFormat.formatted(vmId, message, importance);
            }
            case AuditCategories.VM_CPU -> {
                String messageToFormat = AlertMessageTemplates.VM_CPU_MESSAGE;
                return messageToFormat.formatted(vmId, message, importance);
            }
            case AuditCategories.VM_FILESYSTEM -> {
                String messageToFormat = AlertMessageTemplates.VM_FILE_SYSTEM_MESSAGE;
                return messageToFormat.formatted(vmId, message, importance);
            }
            case AuditCategories.VM_MEMORY -> {
                String messageToFormat = AlertMessageTemplates.VM_MEMORY_MESSAGE;
                return messageToFormat.formatted(vmId, message, importance);
            }
            case AuditCategories.VM_USERS -> {
                String messageToFormat = AlertMessageTemplates.VM_USERS_MESSAGE;
                return messageToFormat.formatted(vmId, message, importance);
            }
            default -> {
                return "";
            }
        }
    }
}
