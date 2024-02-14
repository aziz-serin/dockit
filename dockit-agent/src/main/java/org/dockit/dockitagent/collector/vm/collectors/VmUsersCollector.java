package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.utils.InformationBuilderHelper;
import oshi.SystemInfo;
import oshi.software.os.OSSession;
import oshi.software.os.OperatingSystem;

import java.util.List;
import java.util.Map;

public class VmUsersCollector implements Collector {

    @Override
    public String collect() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        List<OSSession> sessions = operatingSystem.getSessions();
        List<String> sessionInformation = sessions.stream()
                .map(session -> {
                    Gson gson = new Gson();
                    Map<String, ?> data = Map.of(
                            "user_name", session.getUserName(),
                            "host", session.getHost(),
                            "login_time", session.getLoginTime()
                    );
                    return gson.toJson(data);
                })
                .toList();
        return InformationBuilderHelper.build(sessionInformation);
    }
}
