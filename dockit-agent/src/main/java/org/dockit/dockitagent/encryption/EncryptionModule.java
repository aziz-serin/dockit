package org.dockit.dockitagent.encryption;

import com.google.inject.AbstractModule;

/**
 * Encryption module for guice bindings of {@link AESGCMEncrypt} class
 */
public class EncryptionModule extends AbstractModule {

    @Override
    public void configure() {
        bind(AESGCMEncrypt.class);
    }
}
