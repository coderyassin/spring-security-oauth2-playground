package org.yascode.springsecsection1.config.security.oauth2;

import org.springframework.stereotype.Component;
import org.yascode.springsecsection1.config.exception.UnknownClientApplicationException;

@Component
public class KeycloakClientResolver {

    private final KeycloakProperties properties;

    public KeycloakClientResolver(KeycloakProperties properties) {
        this.properties = properties;
    }

    public String resolveRegistrationId(String clientApp) {
        String registrationId = properties.clients().get(clientApp);

        if (registrationId == null || registrationId.isBlank()) {
            throw new UnknownClientApplicationException(clientApp);
        }

        return registrationId;
    }
}