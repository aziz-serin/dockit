package org.dockit.dockitserver.entities;

import java.util.UUID;

/**
 * Interface to be shared by all entities of the application
 */
public interface DTO {
    UUID getId();
    void setId(UUID id);
}
