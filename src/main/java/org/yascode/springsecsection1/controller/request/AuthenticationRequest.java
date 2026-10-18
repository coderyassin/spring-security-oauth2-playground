package org.yascode.springsecsection1.controller.request;

public record AuthenticationRequest(
        String username,
        String password) {
}
