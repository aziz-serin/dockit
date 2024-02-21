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
public class VmCpuUsageAnalyzer implements Analyzer {
    private static final String CPU_LOAD = "cpu_load";

    private final AlertGenerator alertGenerator;

    public VmCpuUsageAnalyzer(AlertGenerator alertGenerator) {
        this.alertGenerator = alertGenerator;
    }

    @Override
    public List<Alert> analyze(Audit audit) {
        String data = audit.getData();
        Gson gson = new Gson();
        Map<String, ?> json = gson.fromJson(data, Map.class);

        Double cpuUsage = (Double) json.get(CPU_LOAD);

        Alert.Importance importance = AnalyzingUtils.getImportanceFromPercentage(cpuUsage);

        Optional<Alert> alert = alertGenerator.generateAlert(audit, importance, String.valueOf(cpuUsage));

        return alert.map(List::of).orElseGet(List::of);
    }
}
