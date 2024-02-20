package org.dockit.dockitagent.scheduler;

import com.google.inject.AbstractModule;

/**
 * Guice bindings for {@link JobScheduler}
 */
public class SchedulerModule extends AbstractModule {
    public void configure() {
        bind(JobScheduler.class).asEagerSingleton();
    }
}
