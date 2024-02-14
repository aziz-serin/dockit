package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.utils.InformationBuilderHelper;
import org.dockit.dockitagent.collector.vm.collectors.constants.VmCollectorConstants;
import oshi.SystemInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.List;
import java.util.Map;

/**
 * Utility class to collect information about the filesystem of the system.
 */
public class VmFileSystemCollector implements Collector {

    /**
     * Collect total and free space for each fileStore in the system
     *
     * @return JSON representation of the collected data in string format
     */
    @Override
    public String collect() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        FileSystem fileSystem = operatingSystem.getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores(true);
        List<String> fileStoreInformation = fileStores.stream()
                .map(fileStore -> {
                    Gson gson = new Gson();
                    Map<String, ?> data = Map.of(
                            VmCollectorConstants.FILE_SYSTEM_TOTAL_SPACE, fileStore.getTotalSpace(),
                            VmCollectorConstants.FILE_SYSTEM_FREE_SPACE, fileStore.getFreeSpace()
                    );
                    return gson.toJson(data);
                }).toList();
        return InformationBuilderHelper.build(fileStoreInformation);
    }
}
