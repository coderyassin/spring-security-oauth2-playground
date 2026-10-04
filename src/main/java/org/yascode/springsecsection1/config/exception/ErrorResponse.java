package org.yascode.springsecsection1.config.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
