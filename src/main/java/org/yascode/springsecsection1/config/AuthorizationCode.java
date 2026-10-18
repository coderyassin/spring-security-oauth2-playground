package org.yascode.springsecsection1.config;

import lombok.Builder;

import java.time.Instant;

@Builder
public record AuthorizationCode(
        String registrationId,
        String username,
        String refreshToken,
        Instant expiresAt
) {
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
