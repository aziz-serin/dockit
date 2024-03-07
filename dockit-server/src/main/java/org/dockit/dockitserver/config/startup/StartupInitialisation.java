package org.dockit.dockitserver.config.startup;

import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.services.templates.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Handle required events by the application on startup
 */
@Component
public class StartupInitialisation {
    private static final Logger logger = LoggerFactory.getLogger(StartupInitialisation.class);

    private static final String USERNAME = "USERNAME";
    private final static String PASSWORD = "PASSWORD";

    private final AdminService adminService;
    private final ConfigurableApplicationContext applicationContext;

    /**
     * @param adminService {@link AdminService} to be injected
     * @param applicationContext {@link ConfigurableApplicationContext} to be injected
     */
    public StartupInitialisation(AdminService adminService, ConfigurableApplicationContext applicationContext) {
        this.adminService = adminService;
        this.applicationContext = applicationContext;
    }

    /**
     * Create an admin on application startup if one does not exist, if it does, inform the user by logging it.
     *
     * @param event context refreshing event
     */
    @EventListener
    public void onApplicationStartupEvent(ContextRefreshedEvent event) {
        String userName;
        String password;
        // Close the application context and kill the application if they don't exist
        try {
            userName = System.getenv(USERNAME);
            password = System.getenv(PASSWORD);
        } catch (SecurityException e) {
            logger.error("Host machine does not allow access to env variables");
            applicationContext.close();
            System.exit(-1);
            return;
        }

        // If a specified admin is present in the database, do nothing
        if (adminWithGivenUsernameExists(userName)) {
            return;
        }
        // If another super admin is present other than the specified one, inform and do nothing
        if(anotherSuperAdminIsPresent()) {
            logger.info("{} is not present, but another SUPER admin is present", userName);
            return;
        }
        // If there is no super admin, we need a default one. So create it
        Optional<Admin> admin = EntityCreator.createAdmin(userName, password, Admin.Role.SUPER);
        // Username or Password was null, exit the application
        if (admin.isEmpty()) {
            logger.error("{} and {} are not set for default admin as env variables, set those for proper instantiation" +
                    " of the application", USERNAME, PASSWORD);
            applicationContext.close();
            System.exit(-1);
            return;
        }
        adminService.save(admin.get());
        logger.info("Created the default admin with USERNAME {} and PASSWORD {}", userName, password);
    }

    private boolean adminWithGivenUsernameExists(String userName) {
        Optional<Admin> possibleAdmin = adminService.findByUsername(userName);
        return possibleAdmin.isPresent();
    }

    private boolean anotherSuperAdminIsPresent() {
        List<Admin.Role> admins = adminService.findAll()
                .stream().map(Admin::getPrivilege)
                .toList();
        return admins.contains(Admin.Role.SUPER);
    }
}
