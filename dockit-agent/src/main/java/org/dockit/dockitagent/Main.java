package org.dockit.dockitagent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.dockit.dockitagent.encryption.AESGCMEncrypt;
import org.dockit.dockitagent.config.ConfigModule;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ConfigModule());
        AESGCMEncrypt aesgcmEncrypt = injector.getInstance(AESGCMEncrypt.class);
    }
}