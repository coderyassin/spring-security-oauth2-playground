package org.yascode.springsecsection1.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.yascode.springsecsection1.model.RefreshToken;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUserIdAndRevokedFalse(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM RefreshToken t WHERE t.tokenHash = :hash")
    Optional<RefreshToken> findByTokenHashForUpdate(@Param("hash") String hash);

    @Modifying
    @Query("""
            UPDATE RefreshToken t
            SET t.revoked = true, t.revokedAt = :now
            WHERE t.familyId = :familyId AND t.revoked = false
            """)
    int revokeFamily(@Param("familyId") UUID familyId, @Param("now") Instant now);

    @Modifying
    @Query("""
            UPDATE RefreshToken t
            SET t.revoked = true, t.revokedAt = :now
            WHERE t.user.id = :userId AND t.revoked = false
            """)
    int revokeAllByUser(@Param("userId") Long userId, @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :limit")
    int deleteExpiredBefore(@Param("limit") Instant limit);
}
