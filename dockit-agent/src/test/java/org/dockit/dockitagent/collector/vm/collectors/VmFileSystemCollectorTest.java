package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.vm.collectors.constants.VmCollectorConstants;
import org.dockit.dockitagent.utils.DataSplitter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VmFileSystemCollectorTest {
    public String collectedData;

    @BeforeAll
    public void setup() {
        Collector collector = new VmFileSystemCollector();
        collectedData = collector.collect();
    }

    @Test
    public void collectContainsRequiredKeys() {
        List<String> allData = DataSplitter.split(collectedData);

        //validate the first one, the rest should be the same
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(allData.get(0), Map.class);

        assertThat(data).containsOnlyKeys(
                VmCollectorConstants.FILE_SYSTEM_NAME,
                VmCollectorConstants.FILE_SYSTEM_TOTAL_SPACE,
                VmCollectorConstants.FILE_SYSTEM_FREE_SPACE);
    }

    @Test
    public void collectContainsCorrectValues() {
        List<String> allData = DataSplitter.split(collectedData);

        //validate the first one, the rest should be the same
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(allData.get(0), Map.class);

        // Assume both are larger than a gb
        assertThat((String) data.get(VmCollectorConstants.FILE_SYSTEM_NAME)).isNotBlank();

        assertThat((Double) data.get(VmCollectorConstants.FILE_SYSTEM_FREE_SPACE) / Math.pow(1024, 3))
                .isGreaterThan(1D);

        assertThat((Double) data.get(VmCollectorConstants.FILE_SYSTEM_TOTAL_SPACE) / Math.pow(1024, 3))
                .isGreaterThan(1D);
    }
}
