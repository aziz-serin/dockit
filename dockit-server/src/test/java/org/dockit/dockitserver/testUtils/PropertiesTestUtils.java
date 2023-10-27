package org.dockit.dockitserver.testUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class PropertiesTestUtils {
    public void createPropertiesFile(String path, String configFileName, Properties properties) {
        String writePath = path + configFileName;
        try {
            FileOutputStream outputStream = new FileOutputStream(writePath);
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
            properties.store(outputStream, "System Config \n Generated at " + timeStamp);
        } catch (IOException e) {
            // ignore
        }
    }
}
