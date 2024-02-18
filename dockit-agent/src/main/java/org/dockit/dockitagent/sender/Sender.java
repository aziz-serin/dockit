package org.dockit.dockitagent.sender;

import org.dockit.dockitagent.entity.Audit;

public interface Sender {
    boolean send(Audit audit);
}
