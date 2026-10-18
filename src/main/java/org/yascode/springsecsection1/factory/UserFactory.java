package org.yascode.springsecsection1.factory;

import org.springframework.stereotype.Component;
import org.yascode.springsecsection1.model.User;

@Component
public class UserFactory {

    public User createEnabled(String subject, String email) {
        return build(subject, email, true);
    }

    public User createDisabled(String subject, String email) {
        return build(subject, email, false);
    }

    public User createEnabled(String userId, String subject, String email) {
        return build(userId, subject, email, true);
    }

    public User createDisabled(String userId, String subject, String email) {
        return build(userId, subject, email, false);
    }

    public User createEnabled(String userId, String username, String subject, String email) {
        return build(userId, username, subject, email, true);
    }

    public User createDisabled(String userId, String username, String subject, String email) {
        return build(userId, username, subject, email, false);
    }

    public User create(String subject, String email, boolean enabled) {
        return build(subject, subject, email, enabled);
    }

    public User create(String username, String subject, String email, boolean enabled) {
        return build(username, subject, email, enabled);
    }

    public User create(String userId, String username, String subject, String email, boolean enabled) {
        return build(userId, username, subject, email, enabled);
    }

    private User build(String subject, String email, boolean enabled) {
        return User.builder()
                .username(email)
                .subject(subject)
                .email(email)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(enabled)
                .build();
    }

    private User build(String username, String subject, String email, boolean enabled) {
        return User.builder()
                .username(username)
                .subject(subject)
                .email(email)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(enabled)
                .build();
    }

    private User build(String userId, String username, String subject, String email, boolean enabled) {
        return User.builder()
                .userId(userId)
                .username(username)
                .subject(subject)
                .email(email)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(enabled)
                .build();
    }
}
