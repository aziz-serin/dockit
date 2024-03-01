package org.dockit.dockitserver.events.event;

import org.dockit.dockitserver.entities.Agent;

/**
 * Event to be published for user intrusion
 *
 * @param agent {@link Agent} that detected the intrusion
 * @param userName name of the intruder
 */
public record UserIntrusionEvent(Agent agent, String userName) {
}
