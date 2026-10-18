package org.yascode.springsecsection1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yascode.springsecsection1.config.JwtHelper;
import org.yascode.springsecsection1.config.RefreshTokenService;
import org.yascode.springsecsection1.config.exception.AccountDisabledException;
import org.yascode.springsecsection1.config.exception.InvalidCredentialsException;
import org.yascode.springsecsection1.controller.response.TokenResponse;
import org.yascode.springsecsection1.model.User;
import org.yascode.springsecsection1.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final KeycloakLoginService keycloakLoginService;
    private final UserRepository userRepository;

    private final JwtHelper jwtHelper;
    private final RefreshTokenService refreshTokenService;

    public TokenResponse login(
            String email,
            String password
    ) {
        keycloakLoginService.authenticate(email, password);

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isEnabled()) {
            throw new AccountDisabledException();
        }

        if (user.getUserId() == null) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtHelper.generateAccessToken(user);
        RefreshTokenService.IssuedToken issuedToken = refreshTokenService.create(user);

        return new TokenResponse(accessToken, issuedToken.rawToken());
    }
}
