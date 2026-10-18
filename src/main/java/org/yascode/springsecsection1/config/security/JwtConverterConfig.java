package org.yascode.springsecsection1.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.yascode.springsecsection1.config.security.oauth2.converter.KeycloakJwtAuthenticationConverter;

@Configuration
@RequiredArgsConstructor
public class JwtConverterConfig {

    private final KeycloakJwtAuthenticationConverter keycloakConverter;

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(keycloakConverter);

        return converter;
    }
}
