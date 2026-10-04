package org.yascode.springsecsection1.config;

import java.time.Instant;

public record AuthorizationCode(
        String username,
        Instant expiresAt
) {
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
