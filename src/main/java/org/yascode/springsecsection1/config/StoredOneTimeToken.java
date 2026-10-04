package org.yascode.springsecsection1.config;

import org.springframework.security.authentication.ott.OneTimeToken;

import java.time.Instant;

public record StoredOneTimeToken(String tokenValue, String username, Instant expiresAt) implements OneTimeToken {

    @Override
    public String getTokenValue() {
        return tokenValue;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Instant getExpiresAt() {
        return expiresAt;
    }
}