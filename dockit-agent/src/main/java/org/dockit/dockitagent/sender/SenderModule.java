package org.dockit.dockitagent.sender;

import com.google.inject.AbstractModule;

/**
 * Guice bindings for {@link Sender}
 */
public class SenderModule extends AbstractModule {
    public void configure() {
        bind(Sender.class).to(AuditSender.class);
    }
}
