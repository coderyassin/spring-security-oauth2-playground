package org.yascode.springsecsection1.config.exception;

public class InvalidAuthorizationCodeException extends RuntimeException {

    public InvalidAuthorizationCodeException() {
        super("Invalid or expired authorization code");
    }

    public InvalidAuthorizationCodeException(String message) {
        super(message);
    }
}
