package org.yascode.springsecsection1.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.web.bind.annotation.*;
import org.yascode.springsecsection1.config.*;
import org.yascode.springsecsection1.config.SessionAttributes;
import org.yascode.springsecsection1.config.exception.InvalidAuthorizationCodeException;
import org.yascode.springsecsection1.config.security.oauth2.KeycloakClientResolver;
import org.yascode.springsecsection1.controller.request.*;
import org.yascode.springsecsection1.controller.response.PasswordSetupConfirmResponse;
import org.yascode.springsecsection1.controller.response.TokenResponse;
import org.yascode.springsecsection1.model.User;
import org.yascode.springsecsection1.service.*;
import org.yascode.springsecsection1.utils.OAuth2RegistrationUtils;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final KeycloakTokenService keycloakTokenService;
    private final KeycloakUserService keycloakUserService;
    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final PasswordSetupService passwordSetupService;

    private final OAuth2RegistrationUtils oAuth2RegistrationUtils;
    private final KeycloakClientResolver keycloakClientResolver;
    private final RedirectUriValidator redirectUriValidator;
    private final AuthorizationCodeStore authorizationCodeStore;

    private final JwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/basic")
    public TokenResponse basicLogin(@RequestBody AuthenticationRequest authenticationRequest) {
        return authenticationService.authenticate(
                authenticationRequest
        );
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                loginService.login(
                        request.email(),
                        request.password()
                )
        );
    }

    @GetMapping("/google")
    public void googleLogin(
            @RequestParam String redirectUri,
            HttpSession session,
            HttpServletResponse response
    ) throws IOException {

        redirectUriValidator.validateRedirectUri(redirectUri);

        session.setAttribute(SessionAttributes.OAUTH2_REDIRECT_URI, redirectUri);

        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/keycloak")
    public void keycloakLogin(
            @RequestParam String clientApp,
            @RequestParam String redirectUri,
            HttpSession session,
            HttpServletResponse response
    ) throws IOException {

        redirectUriValidator.validateRedirectUri(redirectUri);

        String registrationId = keycloakClientResolver.resolveRegistrationId(clientApp);

        session.setAttribute(
                SessionAttributes.OAUTH2_REDIRECT_URI,
                redirectUri
        );

        response.sendRedirect(
                "/oauth2/authorization/"
                        + registrationId
        );
    }

    @PostMapping("/password-setup/confirm")
    public ResponseEntity<PasswordSetupConfirmResponse> confirm(
            @Valid @RequestBody PasswordSetupConfirmRequest request
    ) {
        passwordSetupService.confirmPassword(
                request.code(),
                request.password()
        );

        return ResponseEntity.ok(
                new PasswordSetupConfirmResponse(
                        "Your password has been successfully set up."
                )
        );
    }

    @PostMapping("/exchange")
    public Object exchange(
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

        if (authorizationCode.refreshToken() != null) {

            String registrationId = authorizationCode.registrationId();

            if (registrationId == null) {
                throw new IllegalArgumentException("Unknown OAuth2 registrationId: " + registrationId);
            }

            ClientRegistration registration = oAuth2RegistrationUtils.getRegistration(registrationId);

            return keycloakTokenService.refreshAccessToken(
                    registration.getClientId(),
                    registration.getClientSecret(),
                    authorizationCode.refreshToken());
        } else {
            User user =
                    (User) userDetailsService.loadUserByUsername(
                            authorizationCode.username()
                    );
            String accessToken = jwtHelper.generateAccessToken(user);
            RefreshTokenService.IssuedToken issuedToken = refreshTokenService.create(user);

            return new TokenResponse(accessToken, issuedToken.rawToken());
        }
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

    @PostMapping("/me")
    public ResponseEntity<UserRepresentation> me(@RequestBody UserSearchRequest userSearchRequest) {
        return ResponseEntity.ok(keycloakUserService.findExistingUser(userSearchRequest.email()));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        registrationService.register(request);

        return ResponseEntity.accepted().build();
    }
}
