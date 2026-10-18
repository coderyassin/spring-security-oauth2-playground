package org.yascode.springsecsection1.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "password_setup_tokens",
        indexes = @Index(
                name = "idx_password_setup_token_hash",
                columnList = "token_hash",
                unique = true
        )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PasswordSetupToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;
}
