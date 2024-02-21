package org.dockit.dockitserver.analyze.analyzers;

import com.nimbusds.jose.shaded.gson.Gson;
import org.dockit.dockitserver.analyze.analyzers.utils.AlertGenerator;
import org.dockit.dockitserver.analyze.analyzers.utils.AnalyzingUtils;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class VmMemoryUsageAnalyzer implements Analyzer {
    private static final String TOTAL = "total";
    private static final String AVAILABLE = "available";

    private final AlertGenerator alertGenerator;

    public VmMemoryUsageAnalyzer(AlertGenerator alertGenerator) {
        this.alertGenerator = alertGenerator;
    }

    @Override
    public List<Alert> analyze(Audit audit) {
        String data = audit.getData();
        Gson gson = new Gson();
        Map<String, ?> json = gson.fromJson(data, Map.class);

        Double total = (Double) json.get(TOTAL);
        Double available = (Double) json.get(AVAILABLE);

        double usage = (total - available) / total * 100;

        Alert.Importance importance = AnalyzingUtils.getImportanceFromPercentage(usage);

        if (importance.equals(Alert.Importance.NONE)) {
            return List.of();
        }

        Optional<Alert> alert = alertGenerator.generateAlert(audit, importance, String.valueOf(usage));

        return alert.map(List::of).orElseGet(List::of);
    }
}
