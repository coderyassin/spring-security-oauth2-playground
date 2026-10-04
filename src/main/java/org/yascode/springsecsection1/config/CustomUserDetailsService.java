package org.yascode.springsecsection1.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;

public interface CustomUserDetailsService extends UserDetailsManager {

    UserDetails findOrCreate(
            String subject,
            String email
    );
}
