package org.yascode.springsecsection1.controller.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordSetupConfirmRequest(

        @NotBlank
        String code,

        @NotBlank
        String password
) {
}
