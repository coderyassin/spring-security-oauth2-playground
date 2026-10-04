package org.yascode.springsecsection1.controller.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
