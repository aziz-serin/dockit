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
public class VmNetworkUsageCollectorTest {
    private String collectedData;

    @BeforeAll
    public void setup() {
        Collector collector = new VmNetworkUsageCollector();
        collectedData = collector.collect();
    }

    @Test
    public void collectContainsRequiredKeys() {
        List<String> allData = DataSplitter.split(collectedData);

        //validate the first one, the rest should be the same
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(allData.get(0), Map.class);

        assertThat(data).containsOnlyKeys(
                VmCollectorConstants.NETWORK_PID,
                VmCollectorConstants.NETWORK_FOREIGN_ADDRESS,
                VmCollectorConstants.NETWORK_LOCAL_ADDRESS,
                VmCollectorConstants.NETWORK_FOREIGN_PORT,
                VmCollectorConstants.NETWORK_LOCAL_PORT,
                VmCollectorConstants.NETWORK_TYPE
        );
    }

    @Test
    public void collectContainsCorrectValues() {
        List<String> allData = DataSplitter.split(collectedData);

        //validate the first one, the rest should be the same
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(allData.get(0), Map.class);

        // -1 is given when it is unknown
        assertThat((Double) data.get(VmCollectorConstants.NETWORK_PID)).isNotEqualTo(-1);
        assertThat((List) data.get(VmCollectorConstants.NETWORK_FOREIGN_ADDRESS)).hasSizeLessThan( 16);
        assertThat((List) data.get(VmCollectorConstants.NETWORK_LOCAL_ADDRESS)).hasSizeLessThan(16);
        assertThat((Double) data.get(VmCollectorConstants.NETWORK_FOREIGN_PORT)).isGreaterThanOrEqualTo(0);
        assertThat((Double) data.get(VmCollectorConstants.NETWORK_LOCAL_PORT)).isGreaterThanOrEqualTo(0);
        // protocols
        assertThat((String) data.get(VmCollectorConstants.NETWORK_TYPE)).containsAnyOf("tcp", "udp");

    }
}