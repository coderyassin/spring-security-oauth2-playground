package org.yascode.springsecsection1.controller.response;

import java.time.Instant;

public record GeneratedOneTimeTokenResponse(String tokenValue, String username, Instant expiresAt) {
}
