package org.dockit.dockitserver;

import org.dockit.dockitserver.utils.OSUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DockitServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockitServerApplication.class, args);
        OSUtils.OSDetector.getOS();
    }

}
