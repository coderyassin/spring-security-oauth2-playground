package org.yascode.springsecsection1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yascode.springsecsection1.model.PasswordSetupToken;

import java.util.Optional;

public interface PasswordSetupTokenRepository extends JpaRepository<PasswordSetupToken, String> {

    Optional<PasswordSetupToken> findByTokenHash(String tokenHash);
}
