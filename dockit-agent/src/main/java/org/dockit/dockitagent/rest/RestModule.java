package org.dockit.dockitagent.rest;

import com.google.inject.AbstractModule;

public class RestModule extends AbstractModule {
    public void configure() {
        bind(RestServer.class).asEagerSingleton();
    }
}
