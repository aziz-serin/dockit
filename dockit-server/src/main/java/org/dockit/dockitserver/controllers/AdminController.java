package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.controllers.utils.ParameterValidator;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.services.templates.AdminService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

@RestController
@RequestMapping(path = "/api/admin", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getAdmins() {
        List<Admin> admins = adminService.findAll();
        return ResponseEntity.ok().body(admins);
    }

    @PutMapping("/updateUsername")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> updateAdminUsername(@RequestBody @NonNull Map<String, ?> body) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = (String) body.get("username");
        String newUserName = (String) body.get("new_user_name");
        if (!ParameterValidator.valid(userName, newUserName)) {
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

    @PutMapping("/updatePassword")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> updateAdminPassword(@RequestBody @NonNull Map<String, ?> body) {
        String userName = (String) body.get("username");
        String newPassword = (String) body.get("new_password");
        if (!ParameterValidator.valid(userName, newPassword)) {
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

    @PutMapping("/updateRole")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> updateAdminRole(@RequestBody @NonNull Map<String, ?> body) {
        String userName = (String) body.get("username");
        String newRole = (String) body.get("new_role");

        if (!ParameterValidator.valid(userName, newRole)) {
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

    @PostMapping
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> createAdmin(@RequestBody @NonNull Map<String, ?> body) {
        String userName = (String) body.get("username");
        String password = (String) body.get("password");
        String bodyRole = (String) body.get("role");

        if (!ParameterValidator.valid(userName, password, bodyRole)) {
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
