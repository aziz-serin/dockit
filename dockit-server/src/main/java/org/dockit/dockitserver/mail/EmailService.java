package org.dockit.dockitserver.mail;

import org.dockit.dockitserver.entities.Alert;

public interface EmailService {
    void sendEmail(Alert alert, String to, Alert.Importance defaultImportance);
}
