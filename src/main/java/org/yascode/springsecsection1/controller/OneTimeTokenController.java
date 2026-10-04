package org.yascode.springsecsection1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yascode.springsecsection1.controller.response.GeneratedOneTimeTokenResponse;
import org.yascode.springsecsection1.controller.response.OneTimeTokenLoginResponse;
import org.yascode.springsecsection1.repository.UserRepository;

import java.time.Duration;

@RestController
public class OneTimeTokenController {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(5);

    private final OneTimeTokenService oneTimeTokenService;
    private final UserRepository userRepository;

    public OneTimeTokenController(OneTimeTokenService oneTimeTokenService, UserRepository userRepository) {
        this.oneTimeTokenService = oneTimeTokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/ott/generate")
    public GeneratedOneTimeTokenResponse generate(
            @RequestHeader(value = "username", required = false) String usernameHeader,
            @RequestParam(value = "username", required = false) String usernameParam) {

        String username = StringUtils.hasText(usernameHeader) ? usernameHeader : usernameParam;
        if (!StringUtils.hasText(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is required");
        }

        if (!userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        OneTimeToken token = oneTimeTokenService.generate(new GenerateOneTimeTokenRequest(username, TOKEN_TTL));
        return new GeneratedOneTimeTokenResponse(token.getTokenValue(), token.getUsername(), token.getExpiresAt());
    }

    @PostMapping("/login/ott")
    public OneTimeTokenLoginResponse login(
            @RequestHeader(value = "token", required = false) String token,
            Authentication authentication) {

        if (!StringUtils.hasText(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "token is required");
        }

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid one-time token");
        }

        return new OneTimeTokenLoginResponse(authentication.getName());
    }
}
