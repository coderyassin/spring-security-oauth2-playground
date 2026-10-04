package org.yascode.springsecsection1.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "one_time_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OneTimeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Long version;

    @Column(name = "token_value", nullable = false, unique = true, length = 255)
    private String tokenValue;

    @Column(name = "username", nullable = false, length = 150)
    private String username;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @Column(name = "token_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
}
