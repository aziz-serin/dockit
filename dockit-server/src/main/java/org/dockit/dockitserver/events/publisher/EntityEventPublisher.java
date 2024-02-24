package org.dockit.dockitserver.events.publisher;

import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.events.event.AlertCreationEvent;
import org.dockit.dockitserver.events.event.AuditCreationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publisher class to publish entity-related events
 */
@Component
public class EntityEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * @param eventPublisher {@link ApplicationEventPublisher} instance to be injected
     */
    @Autowired
    public EntityEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creation event is published after the audit is saved in the database
     *
     * @param audit {@link Audit} instance which was saved
     */
    public void publishAuditCreationEvent(Audit audit) {
        eventPublisher.publishEvent(new AuditCreationEvent(audit));
    }

    /**
     * Creation event is published after the alert is saved in the database
     *
     * @param alert {@link Alert} instance which was saved
     */
    public void publishAlertCreationEvent(Alert alert) {
        eventPublisher.publishEvent(new AlertCreationEvent(alert));
    }
}
