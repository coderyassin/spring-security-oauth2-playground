package org.yascode.springsecsection1.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/welcome")
    @PreAuthorize("hasAuthority('UPDATE')")
    public String sayWelcome() {
        return "Welcome to Spring Application with security";
    }
}
