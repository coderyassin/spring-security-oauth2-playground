package org.yascode.springsecsection1.config.security.web.access.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthorizationListener {

    @EventListener
    public void handleAuthorizationGranted(AuthorizationGrantedEvent<?> event) {
        log.info("Access granted for user: {}", event.getAuthentication().get().getName());
        log.info("Target object: {}", event.getObject());
        log.info("Reason/Result: {}", event.getAuthorizationResult());
    }

    @EventListener
    public void handleAuthorizationDenied(AuthorizationDeniedEvent<?> event) {
        log.error("Access denied for user: {}", event.getAuthentication().get().getName());
        log.error("Target object: {}", event.getObject());
        log.error("Reason/Result: {}", event.getAuthorizationResult());
    }
}
