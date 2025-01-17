package org.dockit.dockitagent.collector.docker.collectors;

import com.google.gson.Gson;
import com.google.inject.Injector;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.utils.DataSplitter;
import org.dockit.dockitagent.utils.GuiceTestInitialise;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// Uncomment below to ignore this test case if you do not have a docker engine api running or you are not interested in
// docker running functionality
//@Ignore("Ignored because docker engine not available/not used")
public class DockerContainerFileSystemCollectorTest {

    private Collector collector;

    @Before
    public void setup() {
        Injector injector = GuiceTestInitialise.injector();
        collector = injector.getInstance(DockerContainerFileSystemCollector.class);
    }

    @Test
    public void collectReturnsData() {
        String data = collector.collect();

        assertThat(data).isNotBlank();
    }

    @Test
    public void collectReturnsJsonParsableData() {
        String data = collector.collect();

        List<String> dataList = DataSplitter.split(data);

        assertThat(dataList).isNotEmpty();

        Gson gson = new Gson();
        List<Map<String, String>> dataJson = gson.fromJson(dataList.get(0), List.class);
        //Id of the running container
        assertThat(dataJson.get(0)).containsKey("Path");
    }
}
