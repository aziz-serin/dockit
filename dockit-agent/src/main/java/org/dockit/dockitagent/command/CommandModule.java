package org.dockit.dockitagent.command;

import com.google.inject.AbstractModule;

/**
 * Guice bindings for the command-related classes
 */
public class CommandModule extends AbstractModule {

    public void configure() {
        bind(CommandTranslator.class);
    }
}
