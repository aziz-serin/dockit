package org.dockit.dockitserver.services;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.caching.CacheNames;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.services.templates.AdminService;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminServiceTest {

    private static final String CACHE_NAME = CacheNames.ADMIN;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AdminService adminService;

    private Admin admin1;
    private Admin admin2;
    private Admin admin3;
    private Cache cache;

    @BeforeAll
    public void setup() {
        admin1 = EntityCreator.createAdmin("admin1", "password1", Admin.Role.VIEWER).get();
        adminService.save(admin1);

        admin2 = EntityCreator.createAdmin("admin2", "password2", Admin.Role.EDITOR).get();
        adminService.save(admin2);

        admin3 = EntityCreator.createAdmin("admin3", "password3", Admin.Role.SUPER).get();
        adminService.save(admin3);
        cache = Objects.requireNonNull(cacheManager.getCache(CACHE_NAME));
    }

    @AfterEach
    public void clearCache() {
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME)).clear();
    }

    @Test
    public void getAllReturnAllAdmins() {
        List<Admin> admins = adminService.findAll();

        assertThat(admins.size()).isEqualTo(3);
    }

    @Test
    public void countReturnsTrueCount() {
        assertThat(adminService.count()).isEqualTo(3);
    }

    @Test
    public void deleteDoesNothingIfAdminDoesNotExist() {
        adminService.deleteById(999L);

        assertThat(adminService.count()).isEqualTo(3);
    }

    @Test
    public void deleteDoesDeleteTheAdminIfItExists() {
        adminService.deleteById(admin1.getId());

        assertThat(adminService.count()).isEqualTo(2);

        //Undo the effects of the test
        adminService.save(admin1);
    }

    @Test
    public void existsByIdReturnsTrueIfExists() {
        assertTrue(adminService.existsById(admin1.getId()));
    }

    @Test
    public void existsByIdReturnsFalseIfItDoesNotExists() {
        assertFalse(adminService.existsById(999L));
    }

    @Test
    public void findByIdReturnsEmptyIfIdDoesNotExist() {
        assertFalse(adminService.findById(999L).isPresent());
    }

    @Test
    public void findByIdReturnsAdminIfItExists() {
        Optional<Admin> admin = adminService.findById(admin1.getId());

        assertTrue(admin.isPresent());
        assertThat(admin.get().getId()).isEqualTo(admin1.getId());
    }

    @Test
    public void findByUsernameReturnsEmptyIfIdDoesNotExist() {
        assertFalse(adminService.findByUsername("non-existing").isPresent());
    }

    @Test
    public void findByUsernameReturnsAdminIfItExists() {
        Optional<Admin> admin = adminService.findByUsername(admin1.getUsername());

        assertTrue(admin.isPresent());
        assertThat(admin.get().getId()).isEqualTo(admin1.getId());
    }

    @Test
    public void findAllByIdReturnsEmptyListIfNoIdExists() {
        List<Long> ids = new ArrayList<>(Arrays.asList(999L, 9999L, 99999L));

        assertThat(adminService.findAllById(ids)).hasSize(0);
    }

    @Test
    public void findAllByIdReturnsListIfIdExists() {
        List<Long> ids = new ArrayList<>(Arrays.asList(999L, admin1.getId(), admin2.getId()));

        assertThat(adminService.findAllById(ids)).hasSize(2);
    }

    @Test
    public void findByRoleReturnsAdmin() {
        List<Admin> admins = adminService.findByRole(Admin.Role.SUPER);

        assertThat(admins).hasSize(1);
        assertThat(admins.get(0).getPrivilege()).isEqualTo(Admin.Role.SUPER);
    }

    @Test
    public void updateUsernameReturnsEmptyIfIdDoesNotExist() {
        assertFalse(adminService.updateUsername(999L, "test").isPresent());
    }

    @Test
    public void updateUsernameReturnsEmptyIfNewUsernameExists() {
        assertFalse(adminService.updateUsername(admin1.getId(), admin2.getUsername()).isPresent());
    }

    @Test
    public void updateUsernameUpdatesUsername() {
        Optional<Admin> updatedAdmin = adminService.updateUsername(admin1.getId(), "updatedAdmin1");

        assertTrue(updatedAdmin.isPresent());
        assertThat(updatedAdmin.get().getId()).isEqualTo(admin1.getId());
        assertThat(updatedAdmin.get().getUsername()).isEqualTo("updatedAdmin1");
    }

    @Test
    public void updatePasswordReturnsEmptyIfIdDoesNotExist() {
        assertFalse(adminService.updatePassword(999L, "test").isPresent());
    }

    @Test
    public void updatePasswordUpdatesPassword() {
        Optional<Admin> updatedAdmin = adminService.updatePassword(admin1.getId(), "updatedAdmin1");
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        assertTrue(updatedAdmin.isPresent());
        assertThat(updatedAdmin.get().getId()).isEqualTo(admin1.getId());
        assertTrue(encoder.matches("updatedAdmin1", updatedAdmin.get().getPassword()));
    }

    @Test
    public void updateRoleReturnsEmptyIfIdDoesNotExist() {
        assertFalse(adminService.updateRole(999L, Admin.Role.SUPER).isPresent());
    }

    @Test
    public void updateRoleUpdatesRole() {
        Optional<Admin> updatedAdmin = adminService.updateRole(admin1.getId(), Admin.Role.SUPER);

        assertTrue(updatedAdmin.isPresent());
        assertThat(updatedAdmin.get().getId()).isEqualTo(admin1.getId());
        assertThat(updatedAdmin.get().getPrivilege()).isEqualTo(Admin.Role.SUPER);

        // Undo the effects of the test
        adminService.updateRole(admin1.getId(), Admin.Role.VIEWER);
    }

    @Test
    public void saveUpdatesCache() {
        Admin tempAdmin = EntityCreator.createAdmin("tempAdmin", "password2", Admin.Role.EDITOR).get();
        adminService.save(tempAdmin);

        Admin cachedAdmin = (Admin) cache.get(tempAdmin.getId()).get();

        assertThat(Objects.requireNonNull(cachedAdmin).getId()).isEqualTo(tempAdmin.getId());

        //Undo the effects of this test
        adminService.deleteById(tempAdmin.getId());
    }

    @Test
    public void updateUserNameUpdatesCachedValue() {
        Admin tempAdmin = EntityCreator.createAdmin("tempAdmin", "password2", Admin.Role.EDITOR).get();
        adminService.save(tempAdmin);
        String newUsername = "newTempId";

        adminService.updateUsername(tempAdmin.getId(), newUsername);

        Admin cachedAdmin = (Admin) cache.get(tempAdmin.getId()).get();
        assertThat(Objects.requireNonNull(cachedAdmin).getId()).isEqualTo(tempAdmin.getId());
        assertThat(Objects.requireNonNull(cachedAdmin).getUsername()).isEqualTo(newUsername);

        //Undo the effects of this test
        adminService.deleteById(tempAdmin.getId());
    }

    @Test
    public void updatePasswordUpdatesCachedValue() {
        Admin tempAdmin = EntityCreator.createAdmin("tempAdmin", "password2", Admin.Role.EDITOR).get();
        adminService.save(tempAdmin);
        String newPassword = "newPassword";

        adminService.updatePassword(tempAdmin.getId(), newPassword);
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        Admin cachedAdmin = (Admin) cache.get(tempAdmin.getId()).get();
        assertThat(Objects.requireNonNull(cachedAdmin).getId()).isEqualTo(tempAdmin.getId());
        assertTrue(encoder.matches(newPassword, (Objects.requireNonNull(cachedAdmin).getPassword())));

        //Undo the effects of this test
        adminService.deleteById(tempAdmin.getId());
    }

    @Test
    public void updateRoleUpdatesCachedValue() {
        Admin tempAdmin = EntityCreator.createAdmin("tempAdmin", "password2", Admin.Role.EDITOR).get();
        adminService.save(tempAdmin);
        Admin.Role newRole = Admin.Role.SUPER;

        adminService.updateRole(tempAdmin.getId(), newRole);

        Admin cachedAdmin = (Admin) cache.get(tempAdmin.getId()).get();
        assertThat(Objects.requireNonNull(cachedAdmin).getId()).isEqualTo(tempAdmin.getId());
        assertThat(Objects.requireNonNull(cachedAdmin).getPrivilege()).isEqualTo(newRole);

        //Undo the effects of this test
        adminService.deleteById(tempAdmin.getId());
    }

    @Test
    public void deleteByIdEvictsItemFromCache() {
        Admin tempAdmin = EntityCreator.createAdmin("tempAdmin", "password2", Admin.Role.EDITOR).get();
        adminService.save(tempAdmin);

        adminService.deleteById(tempAdmin.getId());

        Object cachedAdmin = cache.get(tempAdmin.getId());
        assertThat(cachedAdmin).isNull();
    }

    @Test
    public void findByIdCachesResultForSuccessfulFind() {
        //Cache the admin using findById
        adminService.findById(admin3.getId()).get();

        Admin cachedAdmin = (Admin) cache.get(admin3.getId()).get();
        assertThat(Objects.requireNonNull(cachedAdmin).getId()).isEqualTo(admin3.getId());
    }

    @Test
    public void findByIdDoesNotCacheResultForFailedFind() {
        //Cache the admin using findById
        adminService.findById(9999L);

        Object admin =  cache.get(admin3.getId());
        assertThat(admin).isNull();
    }
}