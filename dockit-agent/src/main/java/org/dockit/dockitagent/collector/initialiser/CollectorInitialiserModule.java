package org.dockit.dockitagent.collector.initialiser;

import com.google.inject.AbstractModule;

/**
 * Guice bindings for {@link CollectorInitialiser} class
 */
public class CollectorInitialiserModule extends AbstractModule {
    public void configure() {
        bind(CollectorInitialiser.class);
    }
}
