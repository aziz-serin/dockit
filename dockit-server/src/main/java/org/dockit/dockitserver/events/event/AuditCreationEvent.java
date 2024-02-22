package org.dockit.dockitserver.events.event;

import org.dockit.dockitserver.entities.Audit;

/**
 * Event to be published after saving the audit in the database
 *
 * @param audit {@link Audit instance created}
 */
public record AuditCreationEvent(Audit audit) {
}
