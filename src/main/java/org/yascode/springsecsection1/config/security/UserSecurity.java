package org.yascode.springsecsection1.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.yascode.springsecsection1.model.User;

@Component
public class UserSecurity {

    public boolean isOwner(
            User user,
            Authentication authentication) {

        String returnedUserId = String.valueOf(user.getUserId());
        String authenticatedName = authentication.getName();

        return returnedUserId.equals(authenticatedName);
    }

    public boolean isOwner(
            String userId,
            Authentication authentication) {

        String authenticatedName = authentication.getName();

        return authenticatedName.equals(userId);
    }
}
