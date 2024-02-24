package org.dockit.dockitserver.events.event;

import org.dockit.dockitserver.entities.Alert;

public record AlertCreationEvent(Alert alert) {
}
