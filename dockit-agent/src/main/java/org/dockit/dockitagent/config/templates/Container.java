package org.dockit.dockitagent.config.templates;

import org.dockit.dockitagent.config.Config;

import javax.crypto.SecretKey;

public interface Container {
    SecretKey getKey();
    Config getConfig();
}
