package org.yascode.springsecsection1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yascode.springsecsection1.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
