package org.dockit.dockitserver.analyze.analyzers;

import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;

import java.util.List;

public interface Analyzer {
    List<Alert> analyze(Audit audit);
}
