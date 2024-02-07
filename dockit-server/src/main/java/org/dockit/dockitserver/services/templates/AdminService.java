package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.Admin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer to interact with the Admin object
 */
public interface AdminService {
    /**
     * Save the admin object
     *
     * @param admin {@link Admin} to be saved
     * @return saved {@link Admin}
     */
    Admin save(Admin admin);


    /**
     * Update the admin username
     *
     * @param id id of the {@link Admin} to be updated
     * @param newUsername new username for the admin
     * @return {@link Optional} Admin if admin exists, empty if not
     */
    Optional<Admin> updateUsername(UUID id, String newUsername);

    /**
     * Update the admin password
     *
     * @param id id of the {@link Admin} to be updated
     * @param newPassword new password for the admin
     * @return {@link Optional} Admin if admin exists, empty if not
     */
    Optional<Admin> updatePassword(UUID id, String newPassword);

    /**
     * Update the admin role
     *
     * @param id id of the {@link Admin} to be updated
     * @param role new {@link Admin.Role} for the admin
     * @return {@link Optional} Admin if admin exists, empty if not
     */
    Optional<Admin> updateRole(UUID id, Admin.Role role);

    /**
     * Delete admin using the id
     *
     * @param id {@link Admin} id to be deleted
     */
    void deleteById(UUID id);

    /**
     * Get admin using the id
     *
     * @param id {@link Admin} id to be found
     * @return {@link Optional} Admin if admin exists, empty if not
     */
    Optional<Admin> findById(UUID id);

    /**
     * Get admin using the username
     *
     * @param username {@link Admin} username to be found
     * @return {@link Optional} Admin if admin exists, empty if not
     */
    Optional<Admin> findByUsername(String username);

    /**
     * Get all admins
     *
     * @return list of admins
     */
    List<Admin> findAll();

    /**
     * Return all specified admins using their ids
     *
     * @param ids {@link Admin} ids to be returned
     * @return list of admins
     */
    List<Admin> findAllById(List<UUID> ids);

    /**
     * Return all specified admins using their role
     *
     * @param role {@link Admin} role to be returned
     * @return list of admins
     */
    List<Admin> findByRole(Admin.Role role);

    /**
     * @return count of admins in the database
     */
    long count();

    /**
     * Check if an admin exists in the database using their id
     *
     * @param id {@link Admin} id to be returned
     * @return true if it exists, false if not
     */
    boolean existsById(UUID id);
}
