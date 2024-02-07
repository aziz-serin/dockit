package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.Audit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer to interact with the Audit object
 */
public interface AuditService {
    /**
     * Save the audit object
     *
     * @param audit {@link Audit} to be saved
     * @return saved {@link Audit}
     */
    Audit save(Audit audit);

    /**
     * Delete audit using the id
     *
     * @param id {@link Audit} id to be deleted
     */
    void deleteById(UUID id);

    /**
     * Delete all with the ids
     *
     * @param ids list of {@link Audit} ids to be deleted
     */
    void deleteAllById(List<UUID> ids);

    /**
     * Find audit entry by its id
     *
     * @param id {@link Audit} id to be found
     * @return {@link Optional} empty if not found, audit if it exists
     */
    Optional<Audit> findById(UUID id);

    /**
     * Find audits by their vmIds
     *
     * @param vmId vm id string to filter through audits
     * @return list of audits
     */
    List<Audit> findByVmId(String vmId);

    /**
     * Find audits by their categories
     *
     * @param category category string to filter through audits
     * @return list of audits
     */
    List<Audit> findByCategory(String category);

    /**
     * Find audits by their category and vm ids
     *
     * @param category category string to filter through audits
     * @param vmId vm id string to filter through audits
     * @return list of audits
     */
    List<Audit> findByCategoryAndVmId(String category, String vmId);

    /**
     * Return all audits sorted by timeStamp in ascending order
     *
     * @return list of audits
     */
    List<Audit> findAllSortByTimeStampAscending();

    /**
     * Return all audits sorted by timeStamp in descending order
     *
     * @return list of audits
     */
    List<Audit> findAllSortByTimeStampDescending();

    /**
     * Return all audits with the same category sorted by timeStamp in ascending order
     *
     * @param category category string to filter through audits
     * @return list of audits
     */
    List<Audit> findAllByCategorySortByTimeStampAscending(String category);

    /**
     * Return all audits with the same category sorted by timeStamp in descending order
     *
     * @param category category string to filter through audits
     * @return list of audits
     */
    List<Audit> findAllByCategorySortByTimeStampDescending(String category);

    /**
     * Return all audits with the same vm id sorted by timeStamp in ascending order
     *
     * @param vmId vm id string to filter through audits
     * @return list of audits
     */
    List<Audit> findAllByVmIdSortByTimeStampAscending(String vmId);

    /**
     * Return all audits with the same vm id sorted by timeStamp in descending order
     *
     * @param vmId vm id string to filter through audits
     * @return list of audits
     */
    List<Audit> findAllByVmIdSortByTimeStampDescending(String vmId);

    /**
     * Return all audits with the same vm id in a given interval
     *
     * @param vmId vm id string to filter through audits
     * @param startInterval {@link LocalDateTime} start interval
     * @param endInterval {@link LocalDateTime} end interval
     * @return list of audits
     */
    List<Audit> findAllByVmIdInGivenInterval(String vmId, LocalDateTime startInterval, LocalDateTime endInterval);

    /**
     * @return count of audits in the database
     */
    long count();
}
