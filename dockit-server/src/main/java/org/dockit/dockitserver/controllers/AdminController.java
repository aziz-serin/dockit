package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.controllers.utils.ParameterValidator;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.services.templates.AdminService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller containing the endpoints for admin operations
 */
@RestController
@RequestMapping(path = "/api/admin", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AdminController {
    private final AdminService adminService;

    /**
     * @param adminService {@link AdminService} object to be injected
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Returns all admins
     *
     * @return Response entity containing the response for the request
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getAdmins() {
        List<Admin> admins = adminService.findAll();
        return ResponseEntity.ok().body(admins);
    }

    /**
     * Updates given admin username with a new one
     *
     * @param body Should contain parameters: <br>
     *             "username" -> specifying the username to be changed <br>
     *             "new_user_name" -> specifying the new username <br>
     * @return Response entity containing the response
     */
    @PutMapping("/updateUsername")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> updateAdminUsername(@RequestBody @NonNull Map<String, ?> body) {
        String userName = (String) body.get("username");
        String newUserName = (String) body.get("new_user_name");
        if (ParameterValidator.invalid(userName, newUserName)) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        Optional<Admin> requestedAdmin = adminService.findByUsername(userName);
        if (requestedAdmin.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        Optional<Admin> updatedAdmin = adminService.updateUsername(requestedAdmin.get().getId(), newUserName);
        if (updatedAdmin.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
    }

    /**
     * Updates given admin password with a new one
     *
     * @param body Should contain parameters: <br>
     *             "username" -> specifying the user <br>
     *             "new_password" -> specifying the new password for the given user <br>
     * @return Response entity containing the response
     */
    @PutMapping("/updatePassword")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> updateAdminPassword(@RequestBody @NonNull Map<String, ?> body) {
        String userName = (String) body.get("username");
        String newPassword = (String) body.get("new_password");
        if (ParameterValidator.invalid(userName, newPassword)) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        Optional<Admin> requestedAdmin = adminService.findByUsername(userName);
        if (requestedAdmin.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        Optional<Admin> updatedAdmin = adminService.updatePassword(requestedAdmin.get().getId(), newPassword);
        if (updatedAdmin.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
    }

    /**
     * Updates an admin role with a new one
     *
     * @param body Should contain parameters: <br>
     *             "username" -> specifying the user <br>
     *             "new_role" -> specifying the role to be changed to <br>
     * @return Response entity containing the response
     */
    @PutMapping("/updateRole")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> updateAdminRole(@RequestBody @NonNull Map<String, ?> body) {
        String userName = (String) body.get("username");
        String newRole = (String) body.get("new_role");

        if (ParameterValidator.invalid(userName, newRole)) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }

        Optional<Admin> requestedAdmin = adminService.findByUsername(userName);
        if (requestedAdmin.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }

        Optional<Admin.Role> role = getRole(newRole);
        if (role.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }

        Optional<Admin> updatedAdmin = adminService.updateRole(requestedAdmin.get().getId(), role.get());

        if (updatedAdmin.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
    }

    /**
     * Creates a new admin
     *
     * @param body Should contain parameters: <br>
     *             "username" -> username for the new user <br>
     *             "password" -> password for the new user <br>
     *             "role" -> role for the new user
     * @return Response entity containing the response
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> createAdmin(@RequestBody @NonNull Map<String, ?> body) {
        String userName = (String) body.get("username");
        String password = (String) body.get("password");
        String bodyRole = (String) body.get("role");

        if (ParameterValidator.invalid(userName, password, bodyRole)) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }

        Optional<Admin.Role> role = getRole(bodyRole);
        if (role.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }

        Optional<Admin> admin = EntityCreator.createAdmin(userName, password, role.get());
        //We already null-checked so it is safe to use just get
        adminService.save(admin.get());
        return ResponseEntity.ok().build();

    }

    /**
     * Delete a given user with their username
     *
     * @param userName username of the user to be deleted
     * @return Response entity containing the response
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> deleteAdmin(@RequestParam(name = "userName") @NonNull String userName) {
        Optional<Admin> requestedAdmin = adminService.findByUsername(userName);
        if (requestedAdmin.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        adminService.deleteById(requestedAdmin.get().getId());
        return ResponseEntity.ok().build();
    }

    private Optional<Admin.Role> getRole(String role) {
        if (role == null)
            return Optional.empty();
        else {
            return switch (role) {
                case "SUPER" -> Optional.of(Admin.Role.SUPER);
                case "VIEWER" -> Optional.of(Admin.Role.VIEWER);
                case "EDITOR" -> Optional.of(Admin.Role.EDITOR);
                default -> Optional.empty();
            };
        }
    }
}
