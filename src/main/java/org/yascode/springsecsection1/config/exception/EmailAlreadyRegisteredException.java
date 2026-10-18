package org.yascode.springsecsection1.config.exception;

public class EmailAlreadyRegisteredException extends RuntimeException{

    public EmailAlreadyRegisteredException() {
        super("The email is already registered.");
    }
}
