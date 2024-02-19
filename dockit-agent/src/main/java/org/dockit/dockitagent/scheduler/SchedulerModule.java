package org.dockit.dockitagent.scheduler;

import com.google.inject.AbstractModule;

public class SchedulerModule extends AbstractModule {
    public void configure() {
        bind(JobScheduler.class).asEagerSingleton();
    }
}
