package org.yascode.springsecsection1.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.yascode.springsecsection1.config.*;
import org.yascode.springsecsection1.config.exception.InvalidAuthorizationCodeException;
import org.yascode.springsecsection1.controller.request.ExchangeCodeRequest;
import org.yascode.springsecsection1.controller.response.TokenResponse;
import org.yascode.springsecsection1.model.User;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final RedirectUriValidator redirectUriValidator;
    private final AuthorizationCodeStore authorizationCodeStore;

    private final JwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/google")
    public void googleLogin(
            @RequestParam String redirectUri,
            HttpSession session,
            HttpServletResponse response
    ) throws IOException {

        redirectUriValidator.validateRedirectUri(redirectUri);

        session.setAttribute("OAUTH2_REDIRECT_URI", redirectUri);

        response.sendRedirect("/oauth2/authorization/google");
    }

    @PostMapping("/exchange")
    public TokenResponse exchange(
            @RequestBody ExchangeCodeRequest request
    ) {
        AuthorizationCode authorizationCode =
                authorizationCodeStore.consume(request.code());

        if (authorizationCode == null) {
            throw new InvalidAuthorizationCodeException();
        }

        if (authorizationCode.isExpired()) {
            throw new InvalidAuthorizationCodeException();
        }

        User user =
                (User) userDetailsService.loadUserByUsername(
                        authorizationCode.username()
                );

        String accessToken = jwtHelper.generateAccessToken(user);
        RefreshTokenService.IssuedToken issuedToken = refreshTokenService.create(user);

        return new TokenResponse(accessToken, issuedToken.rawToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            @RequestHeader("refresh_token") String refreshToken
    ) {
        RefreshTokenService.TokenPair pair = refreshTokenService.rotate(refreshToken);

        return ResponseEntity.ok()
                .body(Map.of(
                        "access_token", pair.accessToken(),
                        "refresh_token", pair.refreshToken().rawToken())
                );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("refresh_token") String refreshToken
    ) {

        refreshTokenService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }
}
