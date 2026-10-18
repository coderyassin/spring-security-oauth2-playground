package org.yascode.springsecsection1.config.exception;

public class UnknownClientApplicationException extends RuntimeException {

    public UnknownClientApplicationException(String clientApp) {
        super("Unknown client application: " + clientApp);
    }
}
