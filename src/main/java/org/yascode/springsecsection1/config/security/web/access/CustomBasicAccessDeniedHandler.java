package org.yascode.springsecsection1.config.security.web.access;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
public class CustomBasicAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomBasicAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        response.setHeader("denied-reason", "Authorization Failed");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> payload = Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.FORBIDDEN.name(),
                "error", HttpStatus.FORBIDDEN.getReasonPhrase(),
                "message", "Access Denied: You do not have the required permissions to access this resource.",
                "path", request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), payload);
    }
}
