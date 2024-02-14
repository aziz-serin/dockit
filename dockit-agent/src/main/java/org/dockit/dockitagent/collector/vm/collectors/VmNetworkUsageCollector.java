package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.utils.InformationBuilderHelper;
import oshi.SystemInfo;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.OperatingSystem;

import java.util.List;
import java.util.Map;

public class VmNetworkUsageCollector implements Collector {

    @Override
    public String collect() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        InternetProtocolStats internetProtocolStats = operatingSystem.getInternetProtocolStats();
        List<InternetProtocolStats.IPConnection> connections = internetProtocolStats.getConnections();
        List<String> connectionInformation = connections.stream()
                .map(connection -> {
                    Gson gson = new Gson();
                    Map<String, ?> data = Map.of(
                            "pid", connection.getowningProcessId(),
                            "foreign_address", connection.getForeignAddress(),
                            "local_address", connection.getLocalAddress(),
                            "foreign_port", connection.getForeignPort(),
                            "local_port", connection.getLocalPort(),
                            "type", connection.getType()
                    );
                    return gson.toJson(data);
                }).toList();
        return InformationBuilderHelper.build(connectionInformation);
    }
}
