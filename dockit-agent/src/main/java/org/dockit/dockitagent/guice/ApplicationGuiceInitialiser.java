package org.dockit.dockitagent.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dockit.dockitagent.collector.initialiser.CollectorInitialiserModule;
import org.dockit.dockitagent.config.ConfigModule;
import org.dockit.dockitagent.connection.ConnectionModule;
import org.dockit.dockitagent.encryption.EncryptionModule;
import org.dockit.dockitagent.jobs.JobModule;
import org.dockit.dockitagent.scheduler.JobScheduler;
import org.dockit.dockitagent.scheduler.SchedulerModule;
import org.dockit.dockitagent.sender.SenderModule;

/**
 * Guice configuration for the application
 */
public class ApplicationGuiceInitialiser {
    private final Injector injector;

    /**
     * No-args constructor to initialise the guice {@link Injector} to be used to start the application
     */
    public ApplicationGuiceInitialiser() {
        this.injector = Guice.createInjector(
                new ConfigModule(),
                new ConnectionModule(),
                new EncryptionModule(),
                new SenderModule(),
                new CollectorInitialiserModule(),
                new JobModule(),
                new SchedulerModule()
        );
    }

    /**
     * @return {@link JobScheduler} instance to
     */
    public JobScheduler getJobScheduler() {
        return injector.getInstance(JobScheduler.class);
    }
}
