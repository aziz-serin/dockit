package org.dockit.dockitserver.events.publisher;

import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.events.event.UserIntrusionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publisher class to publish intrusion events
 */
@Component
public class IntrusionEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * @param eventPublisher {@link ApplicationEventPublisher} instance to be injected
     */
    @Autowired
    public IntrusionEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Intrusion event is published after detection of an intrusion event
     *
     * @param agent {@link Agent} where the intrusion occurred.
     * @param userName username of the intruder
     */
    public void publishIntrusionEvent(Agent agent, String userName) {
        eventPublisher.publishEvent(new UserIntrusionEvent(agent, userName));
    }
}
