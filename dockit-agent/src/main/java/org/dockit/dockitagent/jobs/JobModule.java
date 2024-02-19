package org.dockit.dockitagent.jobs;

import com.google.inject.AbstractModule;

public class JobModule extends AbstractModule {
    public void configure() {
        bind(JobDetailsGenerator.class);
    }
}
