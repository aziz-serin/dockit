package org.dockit.dockitserver.analyze.analyzers;

import com.nimbusds.jose.shaded.gson.Gson;
import org.dockit.dockitserver.analyze.analyzers.utils.AlertGenerator;
import org.dockit.dockitserver.analyze.analyzers.utils.AnalyzingUtils;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class VmFileSystemUsageAnalyzer implements Analyzer {
    private static final String NAME = "name";
    private static final String FREE_SPACE = "free_space";
    private static final String TOTAL_SPACE = "total_space";


    private final AlertGenerator alertGenerator;

    public VmFileSystemUsageAnalyzer(AlertGenerator alertGenerator) {
        this.alertGenerator = alertGenerator;
    }

    @Override
    public List<Alert> analyze(Audit audit) {
        String data = audit.getData();
        List<String> dataSplit = AnalyzingUtils.splitData(data);
        List<Alert> alerts = new ArrayList<>();
        Gson gson = new Gson();

        for (String information : dataSplit) {
            Map<String, ?> json = gson.fromJson(information, Map.class);
            String name = (String) json.get(NAME);
            Double free_space = (Double) json.get(FREE_SPACE);
            Double total_space = (Double) json.get(TOTAL_SPACE);

            double space_usage = (total_space - free_space) / total_space * 100;
            Alert.Importance importance = AnalyzingUtils.getImportanceFromPercentage(space_usage);
            String message = name + ": " + space_usage;

            Optional<Alert> optionalAlert = alertGenerator.generateAlert(audit, importance, message);
            optionalAlert.ifPresent(alerts::add);
        }

        return alerts;
    }
}
