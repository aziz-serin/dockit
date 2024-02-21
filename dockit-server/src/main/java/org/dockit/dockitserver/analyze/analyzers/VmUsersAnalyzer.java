package org.dockit.dockitserver.analyze.analyzers;

import com.nimbusds.jose.shaded.gson.Gson;
import org.dockit.dockitserver.analyze.analyzers.utils.AlertGenerator;
import org.dockit.dockitserver.analyze.analyzers.utils.AlertMessageTemplates;
import org.dockit.dockitserver.analyze.analyzers.utils.AnalyzingUtils;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class VmUsersAnalyzer implements Analyzer {
    private static  String USERNAME = "username";
    private static String HOST = "host";
    private static String LOGIN_TIME = "login_time";

    private final AlertGenerator alertGenerator;

    @Autowired
    public VmUsersAnalyzer(AlertGenerator alertGenerator) {
        this.alertGenerator = alertGenerator;
    }

    @Override
    public List<Alert> analyze(Audit audit) {
        String data  = audit.getData();
        Agent agent = audit.getAgent();
        List<String> splitData = AnalyzingUtils.splitData(data);
        Gson gson = new Gson();
        List<Alert> alerts = new ArrayList<>();

        for (String information : splitData) {
            Map<String, ?> json = gson.fromJson(information, Map.class);
            String userName = (String) json.get(USERNAME);
            String host = (String) json.get(HOST);
            long epoch_time = ((Double) json.get(LOGIN_TIME)).longValue();

            Instant instant = Instant.ofEpochMilli(epoch_time);
            LocalDateTime loginTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

            if (agent.getAllowedUsers().contains(userName)) {
                continue;
            }
            String message = AlertMessageTemplates.VM_USERS_MESSAGE.formatted(audit.getVmId(), userName, host,
                    loginTime.toString(), Alert.Importance.CRITICAL);

            Optional<Alert> generatedAlert = alertGenerator.generateIntrusionAlert(audit, message);
            generatedAlert.ifPresent(alerts::add);
        }
        return alerts;
    }
}
