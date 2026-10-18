package org.yascode.springsecsection1.config.exception;

public class AccountDisabledException
        extends RuntimeException {

    public AccountDisabledException() {
        super("Ce compte est désactivé.");
    }
}
