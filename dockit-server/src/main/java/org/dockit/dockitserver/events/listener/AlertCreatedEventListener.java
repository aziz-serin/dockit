package org.dockit.dockitserver.events.listener;

import org.dockit.dockitserver.config.ConfigContainer;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.events.event.AlertCreationEvent;
import org.dockit.dockitserver.mail.GmailEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener class for the creation of {@link AlertCreationEvent}
 */
@Component
public class AlertCreatedEventListener {
    private final ConfigContainer configContainer;
    private final GmailEmailService emailService;

    /**
     * @param configContainer {@link ConfigContainer} instance to be injected
     * @param emailService {@link GmailEmailService} instance to be injected
     */
    @Autowired
    public AlertCreatedEventListener(ConfigContainer configContainer, GmailEmailService emailService) {
        this.configContainer = configContainer;
        this.emailService = emailService;
    }

    /**
     * Send an email for the saved alert
     *
     * @param alertCreationEvent {@link AlertCreationEvent} instance containing the created Alert
     */
    @EventListener
    public void alertCreatedEventListener(AlertCreationEvent alertCreationEvent) {
        Alert alert = alertCreationEvent.alert();
        emailService.sendEmail(alert, configContainer.getConfig().getSendingEmailAddress(),
                configContainer.getConfig().getImportance());
    }
}
