package org.yascode.springsecsection1.config;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AuthorizationCodeGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
