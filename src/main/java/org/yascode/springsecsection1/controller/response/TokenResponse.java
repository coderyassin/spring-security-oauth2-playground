package org.yascode.springsecsection1.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        long expiresIn,

        @JsonProperty("refresh_expires_in")
        long refreshExpiresIn,

        @JsonProperty("id_token")
        String idToken
) {

    public TokenResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, null, 0, 0, null);
    }
}
