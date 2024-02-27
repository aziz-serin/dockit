package org.dockit.dockitserver.events.publisher;

import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.events.event.UserIntrusionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class IntrusionEventPublisher {

    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public IntrusionEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishIntrusionEvent(Agent agent, String userName) {
        eventPublisher.publishEvent(new UserIntrusionEvent(agent, userName));
    }
}
