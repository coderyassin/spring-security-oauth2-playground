package org.yascode.springsecsection1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.yascode.springsecsection1.service.SoldeService;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
//@CrossOrigin(
//        originPatterns = {"http://localhost:*", "https://localhost:*", "http://*.localhost:*", "https://*.localhost:*"},
//        allowedHeaders = "*",
//        methods = {GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE},
//        exposedHeaders = "*",
//        maxAge = 3600
//)
@Slf4j
public class SomeController {

    private final SoldeService soldeService;

    public SomeController(SoldeService soldeService) {
        this.soldeService = soldeService;
    }


    @GetMapping("/current-user")
    public Authentication currentUser() {

        log.info("Received request to fetch current user profile [method=GET, path=/current-user]");

        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }

    @GetMapping("/username")
    public String currentUsername(Authentication authentication) {

        log.info("Received request to fetch current username [method=GET, path=/username]");

        return authentication.getName();
    }

    @GetMapping("/solde/{username}")
    @PreAuthorize("hasAuthority('READ')")
    public SoldeService.SoldeInfo getSolde(@PathVariable String username) {
        return soldeService.getSolde(username);
    }
}
