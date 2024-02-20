package org.dockit.dockitagent.jobs;

import com.google.inject.AbstractModule;

/**
 * Guice bindings for {@link JobDetailsGenerator}
 */
public class JobModule extends AbstractModule {
    public void configure() {
        bind(JobDetailsGenerator.class);
    }
}
