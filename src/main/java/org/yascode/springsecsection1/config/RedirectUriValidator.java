package org.yascode.springsecsection1.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedirectUriValidator {

    private final OAuth2Properties properties;

    public void validateRedirectUri(String redirectUri) {

        if (redirectUri == null || redirectUri.isBlank()) {
            throw new IllegalArgumentException(
                    "redirect_uri is required"
            );
        }

        if (!properties.getAuthorizedRedirectUris()
                .contains(redirectUri)) {

            throw new IllegalArgumentException(
                    "Unauthorized redirect_uri"
            );
        }
    }
}
