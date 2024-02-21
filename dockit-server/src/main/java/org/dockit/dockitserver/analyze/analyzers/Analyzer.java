package org.dockit.dockitserver.analyze.analyzers;

import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;

import java.util.List;

public interface Analyzer {
    /**
     * Analyze given {@link Audit} data
     *
     * @param audit {@link Audit} data to analyze
     * @return List of Alerts generated for the given audit
     */
    List<Alert> analyze(Audit audit);
}
