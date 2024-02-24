package org.dockit.dockitserver.events.event;

import org.dockit.dockitserver.entities.Alert;

/**
 * Event to be published after saving an alert in the database
 *
 * @param alert {@link Alert instance created}
 */
public record AlertCreationEvent(Alert alert) {
}
