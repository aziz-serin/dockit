package org.dockit.dockitserver.config;

import org.dockit.dockitserver.exceptions.config.ConfigWriterException;
import org.dockit.dockitserver.security.keystore.KeyStoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class ConfigWriter {

    private static final Logger logger = LoggerFactory.getLogger(ConfigWriter.class);

    protected String createRootDirectory(String rootPath) throws ConfigWriterException {
        try {
            Path path = Paths.get(rootPath);

            Files.createDirectories(path);
            logger.info("Directory '{}' is created", rootPath);
            return path.toString();
        } catch (IOException e) {
            logger.error("Failed to create directory!" + e.getMessage());
            throw new ConfigWriterException(e.getMessage());
        }
    }

    protected KeyStore createKeyStore(String path, String keyStoreName, String keyStorePassword) throws ConfigWriterException {
        return KeyStoreManager.createKeystore(keyStoreName, path, keyStorePassword);
    }

    protected Properties createProperties(String path, String configFileName, Properties properties) throws ConfigWriterException {
        String writePath = path + configFileName;
        try {
            FileOutputStream outputStream = new FileOutputStream(writePath);
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
            properties.store(outputStream, "System Config \nGenerated at " + timeStamp);
            return properties;
        } catch (IOException e) {
            logger.debug("Could not create the properties!");
            // ignore exception
            return null;
        }
    }
}
