package org.dockit.dockitserver.mail;

import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;

/**
 * Interface to be implemented by mail services which send {@link Alert} mails
 */
public interface EmailService {
    void sendEmail(Alert alert, String to, Alert.Importance defaultImportance);
    void sendEmail(Agent agent, String to, String message);
}
