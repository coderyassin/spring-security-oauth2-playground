package org.yascode.springsecsection1.config.exception;

public class InvalidCredentialsException
        extends RuntimeException {

    public InvalidCredentialsException() {
        super("Email ou mot de passe incorrect.");
    }
}