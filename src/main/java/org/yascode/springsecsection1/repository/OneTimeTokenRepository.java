package org.yascode.springsecsection1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.yascode.springsecsection1.model.OneTimeToken;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OneTimeTokenRepository extends JpaRepository<OneTimeToken, UUID> {

    Optional<OneTimeToken> findByTokenValueAndUsedFalse(String tokenValue);

    @Modifying
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
