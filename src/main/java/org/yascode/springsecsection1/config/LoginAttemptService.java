package org.yascode.springsecsection1.config;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yascode.springsecsection1.model.User;
import org.yascode.springsecsection1.repository.UserRepository;

import java.time.Instant;

@Service
public class LoginAttemptService {

    private final UserRepository userRepository;
    private final LoginSecurityProperties properties;

    public LoginAttemptService(UserRepository userRepository, LoginSecurityProperties properties) {
        this.userRepository = userRepository;
        this.properties = properties;
    }

    @EventListener
    @Transactional
    public void registerFailure(AuthenticationFailureBadCredentialsEvent event) {
        userRepository.findByUsername(event.getAuthentication().getName())
                .ifPresent(this::incrementFailedAttempts);
    }

    @EventListener
    @Transactional
    public void resetAttempts(AuthenticationSuccessEvent event) {
        userRepository.findByUsername(event.getAuthentication().getName())
                .ifPresent(user -> {
                    user.setFailedLoginAttempts(0);
                    user.setLastFailedLoginAt(null);
                    user.setLockedUntil(null);
                    user.setLastSuccessfulLoginAt(Instant.now());
                });
    }

    private void incrementFailedAttempts(User user) {
        Instant now = Instant.now();
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(now)) {
            return;
        }

        if (user.getLockedUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
        }

        if (user.getLastFailedLoginAt() == null
                || user.getLastFailedLoginAt().plus(properties.getFailedAttemptWindow()).isBefore(now)) {
            user.setFailedLoginAttempts(0);
        }

        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        user.setLastFailedLoginAt(now);
        if (attempts >= properties.getMaxFailedAttempts()) {
            user.setLockedUntil(now.plus(properties.getLockDuration()));
        }
    }
}
