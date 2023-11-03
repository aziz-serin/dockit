package org.dockit.dockitserver.config;

import org.dockit.dockitserver.exceptions.config.ConfigWriterException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConfigWriterTest {

    static final String DIRECTORY_NAME = ".dockit";
    static final String KEY = "example_key";
    static final String VALUE = "example_value";
    static final String FILE_NAME = "test.properties";
    static ConfigWriter configWriter;

    @TempDir
    static Path tempDir;

    @BeforeAll
    public static void setup() {
        configWriter = new ConfigWriter();
    }

    @Test
    public void createRootDirectoryThrowsException() {
        assertThrows(ConfigWriterException.class, () -> {
            configWriter.createRootDirectory("/", DIRECTORY_NAME);
        });
    }

    @Test
    public void createRootDirectoryCreatesDirectory() {
        String path = tempDir.toString() + File.separator;
        String returnedPath = configWriter.createRootDirectory(path, DIRECTORY_NAME);
        File createdDirectory = new File(returnedPath);

        assertThat(returnedPath).isEqualTo(path + DIRECTORY_NAME);
        assertTrue(createdDirectory.isDirectory());
        assertFalse(createdDirectory.isFile());
    }

    @Test
    public void createPropertiesReturnsNullGivenInvalidPath() {
        Properties propertiesToWrite = new Properties();
        propertiesToWrite.put(KEY, VALUE);
        Properties properties = configWriter.createProperties("/", FILE_NAME, propertiesToWrite);

        assertThat(properties).isNull();
    }

    @Test
    public void createPropertiesReturnsProperties() {
        String path = tempDir.toString() + File.separator;
        Properties propertiesToWrite = new Properties();
        propertiesToWrite.put("example_key", "example_value");
        Properties properties = configWriter.createProperties(path, FILE_NAME, propertiesToWrite);
        File createdPropertiesFile = new File(path + FILE_NAME);

        assertThat(properties).hasSize(1);
        assertThat(properties).containsEntry(KEY, VALUE);
        assertTrue(createdPropertiesFile.isFile());
        assertFalse(createdPropertiesFile.isDirectory());
    }
}
