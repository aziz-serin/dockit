package org.dockit.dockitserver.analyze.analyzers;

import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VmUsersAnalyzer implements Analyzer {
    @Override
    public List<Alert> analyze(Audit audit) {
        return null;
    }
}
