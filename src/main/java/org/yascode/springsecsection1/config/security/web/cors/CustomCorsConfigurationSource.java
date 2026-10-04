package org.yascode.springsecsection1.config.security.web.cors;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Component
public class CustomCorsConfigurationSource implements CorsConfigurationSource {

    private final CorsConfiguration config;

    public CustomCorsConfigurationSource(CorsProperties corsProperties) {
        this.config = new CorsConfiguration();

        this.config.setAllowedOriginPatterns(corsProperties.getAllowedOriginPatterns());

        this.config.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name(),
                HttpMethod.HEAD.name()
        ));

        this.config.setAllowedHeaders(List.of(CorsConfiguration.ALL));

        this.config.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.SET_COOKIE));

        this.config.setAllowCredentials(true);
        this.config.setMaxAge(Duration.ofHours(1));
    }

    @Override
    public @Nullable CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
        return this.config;
    }
}
