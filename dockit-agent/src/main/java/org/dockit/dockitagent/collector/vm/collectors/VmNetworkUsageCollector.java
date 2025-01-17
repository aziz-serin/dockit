package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.utils.InformationBuilderHelper;
import org.dockit.dockitagent.collector.vm.collectors.constants.VmCollectorConstants;
import oshi.SystemInfo;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.OperatingSystem;

import java.util.List;
import java.util.Map;

/**
 * Utility class to collect information about the system's network usage
 */
public class VmNetworkUsageCollector implements Collector {

    /**
     * Collect network connection information about the system.
     *
     * @return JSON representation of collected data in string format
     */
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
                            VmCollectorConstants.NETWORK_PID, connection.getowningProcessId(),
                            VmCollectorConstants.NETWORK_FOREIGN_ADDRESS, connection.getForeignAddress(),
                            VmCollectorConstants.NETWORK_LOCAL_ADDRESS, connection.getLocalAddress(),
                            VmCollectorConstants.NETWORK_FOREIGN_PORT, connection.getForeignPort(),
                            VmCollectorConstants.NETWORK_LOCAL_PORT, connection.getLocalPort(),
                            VmCollectorConstants.NETWORK_TYPE, connection.getType()
                    );
                    return gson.toJson(data);
                }).toList();
        return InformationBuilderHelper.build(connectionInformation);
    }
}
