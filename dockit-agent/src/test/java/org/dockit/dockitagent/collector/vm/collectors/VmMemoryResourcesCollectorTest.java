package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.vm.collectors.constants.VmCollectorConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VmMemoryResourcesCollectorTest {

    private String collectedData;

    @BeforeAll
    public void setup() {
        Collector collector = new VmMemoryResourcesCollector();
        collectedData = collector.collect();
    }

    @Test
    public void collectContainsRequiredKeys() {
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(collectedData, Map.class);

        assertThat(data).containsOnlyKeys(VmCollectorConstants.MEMORY_TOTAL,
                VmCollectorConstants.MEMORY_AVAILABLE);
    }

    @Test
    public void collectContainsCorrectValues() {
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(collectedData, Map.class);

        // Assuming more than 100mb of memory
        assertThat((Double) data.get((VmCollectorConstants.MEMORY_TOTAL)) / Math.pow(1024, 2))
                .isGreaterThan(100D);
        assertThat((Double) data.get((VmCollectorConstants.MEMORY_AVAILABLE)) / Math.pow(1024, 2))
                .isGreaterThan(100D);
    }
}