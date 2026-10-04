package org.yascode.springsecsection1.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidAuthorizationCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAuthorizationCode(
            InvalidAuthorizationCodeException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "INVALID_AUTHORIZATION_CODE",
                        exception.getMessage()
                ));
    }
}
