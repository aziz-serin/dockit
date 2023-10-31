package org.dockit.dockitserver.repositories;

import org.dockit.dockitserver.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
