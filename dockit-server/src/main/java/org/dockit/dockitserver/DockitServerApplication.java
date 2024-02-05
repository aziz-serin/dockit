package org.dockit.dockitserver;

import org.dockit.dockitserver.config.utils.OSUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class})
public class DockitServerApplication {

    public static void main(String[] args) {
        OSUtils.OSDetector.getOS();
        SpringApplication.run(DockitServerApplication.class, args);
    }

}
