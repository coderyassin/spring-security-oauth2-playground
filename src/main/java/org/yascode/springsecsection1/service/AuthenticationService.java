package org.yascode.springsecsection1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.yascode.springsecsection1.config.JwtHelper;
import org.yascode.springsecsection1.config.RefreshTokenService;
import org.yascode.springsecsection1.controller.request.AuthenticationRequest;
import org.yascode.springsecsection1.controller.response.TokenResponse;
import org.yascode.springsecsection1.model.User;
import org.yascode.springsecsection1.repository.UserRepository;

@Service
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;

    public AuthenticationService(
            AuthenticationManager authManager,
            RefreshTokenService refreshTokenService,
            JwtHelper jwtHelper,
            UserRepository userRepository
    ) {
        this.authManager = authManager;
        this.refreshTokenService = refreshTokenService;
        this.jwtHelper = jwtHelper;
        this.userRepository = userRepository;
    }

    public TokenResponse authenticate(AuthenticationRequest authenticationRequest) {

        String username = authenticationRequest.username();

        Authentication usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                username,
                authenticationRequest.password()
        );

        authManager.authenticate(usernamePasswordAuthenticationToken);

        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        String accessToken = jwtHelper.generateAccessToken(user);
        RefreshTokenService.IssuedToken issuedToken = refreshTokenService.create(user);

        return new TokenResponse(accessToken, issuedToken.rawToken());
    }

}
