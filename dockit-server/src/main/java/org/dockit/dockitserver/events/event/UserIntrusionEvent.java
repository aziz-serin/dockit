package org.dockit.dockitserver.events.event;

import org.dockit.dockitserver.entities.Agent;

public record UserIntrusionEvent(Agent agent, String userName) {
}
