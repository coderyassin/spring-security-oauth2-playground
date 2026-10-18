package org.yascode.springsecsection1.config.exception;

public class KeycloakAuthenticationException
        extends RuntimeException {

    public KeycloakAuthenticationException() {
        super("The authentication service is unavailable.");
    }
}
