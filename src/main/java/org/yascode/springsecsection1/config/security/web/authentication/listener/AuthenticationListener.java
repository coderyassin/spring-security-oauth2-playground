package org.yascode.springsecsection1.config.security.web.authentication.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationListener {

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        log.info("User {} logged in successfully", successEvent.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
        log.error("Authentication failed for user [{}]. Reason: {}",
                failureEvent.getAuthentication().getName(),
                failureEvent.getException().getMessage());
    }
}
