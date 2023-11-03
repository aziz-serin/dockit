package org.dockit.dockitserver.services;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.services.templates.AdminService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Autowired
    private AdminService adminService;

    private Admin admin1;
    private Admin admin2;
    private Admin admin3;

    @BeforeAll
    public void setup() {
        admin1 = new Admin();
        admin1.setUsername("admin1");
        admin1.setPassword("password1");
        admin1.setPrivilege(Admin.Role.VIEWER);
        adminService.save(admin1);

        admin2 = new Admin();
        admin2.setUsername("admin2");
        admin2.setPassword("password2");
        admin2.setPrivilege(Admin.Role.EDITOR);
        adminService.save(admin2);

        admin3 = new Admin();
        admin3.setUsername("admin3");
        admin3.setPassword("password3");
        admin3.setPrivilege(Admin.Role.SUPER);
        adminService.save(admin3);
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

        assertTrue(updatedAdmin.isPresent());
        assertThat(updatedAdmin.get().getId()).isEqualTo(admin1.getId());
        assertThat(updatedAdmin.get().getPassword()).isEqualTo("updatedAdmin1");
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
}