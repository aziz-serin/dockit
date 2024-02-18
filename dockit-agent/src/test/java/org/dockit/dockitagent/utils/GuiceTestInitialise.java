package org.dockit.dockitagent.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dockit.dockitagent.connection.ConnectionModule;
import org.dockit.dockitagent.config.ConfigModule;
import org.dockit.dockitagent.encryption.EncryptionModule;


/**
 * Add any newly created modules here to be used in testing
 */
public class GuiceTestInitialise {
    public static Injector injector() {
        return Guice.createInjector(
                new ConfigModule(),
                new ConnectionModule(),
                new EncryptionModule()
        );
    }
}