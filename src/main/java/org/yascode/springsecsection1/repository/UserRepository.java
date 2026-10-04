package org.yascode.springsecsection1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.yascode.springsecsection1.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.subject) = LOWER(:subject)
           OR LOWER(u.email) = LOWER(:email)
        """)
    Optional<User> findBySubjectOrEmail(@Param("subject") String subject,
                                         @Param("email") String email);
}
