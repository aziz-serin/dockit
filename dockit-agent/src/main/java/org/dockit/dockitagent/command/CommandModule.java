package org.dockit.dockitagent.command;

import com.google.inject.AbstractModule;

public class CommandModule extends AbstractModule {

    public void configure() {
        bind(CommandTranslator.class);
    }
}
