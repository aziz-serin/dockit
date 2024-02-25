package org.dockit.dockitagent.rest;

import com.google.inject.AbstractModule;
import org.dockit.dockitagent.rest.routes.CommandRoute;

/**
 * Guice bindings for the api-related classes
 */
public class RestModule extends AbstractModule {
    public void configure() {
        bind(RestServer.class).asEagerSingleton();
        bind(CommandRoute.class);
    }
}
