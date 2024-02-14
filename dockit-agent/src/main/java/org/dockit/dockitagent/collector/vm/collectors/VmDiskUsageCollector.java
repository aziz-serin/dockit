package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.utils.InformationBuilderHelper;
import org.dockit.dockitagent.collector.vm.collectors.constants.VmCollectorConstants;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.List;
import java.util.Map;

/**
 * Utility class to collect information about the storage disk of the system
 */
public class VmDiskUsageCollector implements Collector {

    /**
     * Collect name, size and bytes written/read from the disk.
     *
     * @return JSON representation of collected data in string format
     */
    @Override
    public String collect() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        List<HWDiskStore> diskStores = hardware.getDiskStores();
        List<String> diskInformation = diskStores.stream()
                .map(diskStore -> {
                    Gson gson = new Gson();
                    Map<String, ?> data = Map.of(
                            VmCollectorConstants.DISK_STORE_NAME, diskStore.getName(),
                            VmCollectorConstants.DISK_STORE_SIZE, diskStore.getSize(),
                            VmCollectorConstants.DISK_STORE_READ, diskStore.getReadBytes(),
                            VmCollectorConstants.DISK_STORE_WRITE, diskStore.getWriteBytes()
                    );
                    return gson.toJson(data);
                })
                .toList();
        return InformationBuilderHelper.build(diskInformation);
    }
}
