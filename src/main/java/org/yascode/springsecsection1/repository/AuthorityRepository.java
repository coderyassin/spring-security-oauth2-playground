package org.yascode.springsecsection1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yascode.springsecsection1.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
