package org.yascode.springsecsection1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SoldeService {


    @PostAuthorize("returnObject.username() == authentication.principal.username")
    public SoldeInfo getSolde(String username) {
        log.info("Getting solde for user: {}", username);
        return new SoldeInfo("userAdmin", 750000L);
    }

    public record SoldeInfo(String username, Long solde) {}
}
