package org.yascode.springsecsection1.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_user", columnList = "user_id"),
                @Index(name = "idx_refresh_token_family", columnList = "family_id"),
                @Index(name = "idx_refresh_token_expires", columnList = "expires_at")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_refresh_token_hash", columnNames = "token_hash")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "token_hash", nullable = false, length = 64, updatable = false)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "family_id", nullable = false, updatable = false)
    private UUID familyId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "replaced_by_hash", length = 64)
    private String replacedByHash;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (familyId == null) familyId = UUID.randomUUID();
    }

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    public boolean isActive() {
        return !revoked && !isExpired();
    }

    public void revoke(String replacedByHash) {
        this.revoked = true;
        this.revokedAt = Instant.now();
        this.replacedByHash = replacedByHash;
    }

}
