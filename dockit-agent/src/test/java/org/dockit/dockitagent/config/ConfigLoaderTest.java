package org.dockit.dockitagent.config;

import org.dockit.dockitagent.config.templates.ConfigReader;
import org.dockit.dockitagent.exceptions.config.ConfigException;
import org.dockit.dockitagent.utils.PropertiesUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigLoaderTest {
    private static final String PATH = "some_path";
    @Mock
    private ConfigReader propertiesConfigReader;

    @Test
    public void readConfigThrowsExceptionGivenPathNull() {
        ConfigLoader configLoader = new ConfigLoader(propertiesConfigReader);
        assertThrows(ConfigException.class, () -> {
            configLoader.readConfig(null);
        });
    }

    @Test
    public void readConfigThrowsExceptionGivenEmptyProperties() {
        when(propertiesConfigReader.read(eq(PATH))).thenReturn(Optional.empty());
        ConfigLoader configLoader = new ConfigLoader(propertiesConfigReader);

        assertThrows(ConfigException.class, () -> {
            configLoader.readConfig(PATH);
        });
    }

    @Test
    public void readConfigDoesNotThrowException() throws ConfigException {
        Properties properties = PropertiesUtils.generateMockProperties();

        when(propertiesConfigReader.read(PATH)).thenReturn(Optional.of(properties));
        ConfigLoader configLoader = new ConfigLoader(propertiesConfigReader);

        configLoader.readConfig(PATH);
    }
}