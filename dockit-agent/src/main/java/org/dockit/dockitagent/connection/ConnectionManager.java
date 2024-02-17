package org.dockit.dockitagent.connection;

import java.util.Optional;

public interface ConnectionManager {
    boolean isAlive();
    Optional<String> sendRequest(String endPoint);
}
