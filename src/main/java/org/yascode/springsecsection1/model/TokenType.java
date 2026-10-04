package org.yascode.springsecsection1.model;

import lombok.Getter;

@Getter
public enum TokenType {

    EMAIL_VERIFICATION("Email verification"),

    PASSWORD_RESET("Password reset"),

    MFA_AUTH("Multi-factor authentication (MFA)"),

    SENSITIVE_ACTION_VALIDATION("Sensitive action validation"),

    PASSWORDLESS_LOGIN("Magic login link");

    private final String description;

    TokenType(String description) {
        this.description = description;
    }
}
