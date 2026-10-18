package org.yascode.springsecsection1.config.exception;

public class InvalidPasswordSetupTokenException
        extends RuntimeException {

    public InvalidPasswordSetupTokenException() {
        super("Le code est invalide ou expiré.");
    }
}
