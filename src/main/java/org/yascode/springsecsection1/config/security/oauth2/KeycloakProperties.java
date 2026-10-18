package org.yascode.springsecsection1.config.security.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "app.keycloak")
public record KeycloakProperties(
        Map<String, String> clients
) {
}
