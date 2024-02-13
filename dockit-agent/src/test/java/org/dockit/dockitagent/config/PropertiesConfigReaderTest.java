package org.dockit.dockitagent.config;

import org.dockit.dockitagent.config.templates.ConfigReader;
import org.dockit.dockitagent.exceptions.config.ConfigException;
import org.dockit.dockitagent.utils.PropertiesUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;


class PropertiesConfigReaderTest {

    private static final String FILE_NAME = "config.properties";

    private final ConfigReader propertiesConfigReader = new PropertiesConfigReader();

    @TempDir
    Path tempDir;

    @Test
    void readReturnsEmptyGivenIOFailure() {
        Optional<Properties> properties = propertiesConfigReader.read("someWrongPath");

        assertThat(properties).isEmpty();
    }

    @Test
    void readReturnsProperties() {
        String path = tempDir.toString() + File.separator;
        Properties generatedProperties = PropertiesUtils.generateMockProperties();
        String pathToConfigFile = PropertiesUtils.write(path, FILE_NAME, generatedProperties);

        Optional<Properties> properties = propertiesConfigReader.read(pathToConfigFile);

        assertThat(properties).isPresent();
        assertThat(properties.get()).containsAllEntriesOf(generatedProperties);
    }

    @Test
    void generateConfigThrowsConfigExceptionGivenInvalidProperties() {
        Properties wrongProperties = new Properties();

        assertThrows(ConfigException.class, () -> {
            propertiesConfigReader.generateConfig(wrongProperties);
        });
    }

    @Test
    void generateConfigGeneratesConfig() throws ConfigException {
        Properties properties = PropertiesUtils.generateMockProperties();

        propertiesConfigReader.generateConfig(properties);

        assertThat(Config.INSTANCE.getInstance().getID()).isEqualTo(properties.get(ConfigConstants.ID));
        assertThat(Config.INSTANCE.getInstance().getKEY()).isEqualTo(properties.get(ConfigConstants.KEY));
        assertThat(Config.INSTANCE.getInstance().getINTERVAL())
                .isEqualTo(Integer.parseInt((String) properties.get(ConfigConstants.INTERVAL)));
        assertThat(Config.INSTANCE.getInstance().isDOCKER())
                .isEqualTo(Boolean.parseBoolean((String) properties.get(ConfigConstants.DOCKER)));
        assertThat(Config.INSTANCE.getInstance().isDOCKER())
                .isEqualTo(Boolean.parseBoolean((String) properties.get(ConfigConstants.VM_DATA)));
    }
}