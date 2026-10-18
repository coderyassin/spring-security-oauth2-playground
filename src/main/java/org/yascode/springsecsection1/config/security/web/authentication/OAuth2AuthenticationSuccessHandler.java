package org.yascode.springsecsection1.config.security.web.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.yascode.springsecsection1.config.*;
import org.yascode.springsecsection1.service.KeycloakUserService;
import org.yascode.springsecsection1.service.PasswordSetupLinkService;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final KeycloakUserService keycloakUserService;
    private final PasswordSetupLinkService passwordSetupLinkService;
    private final CustomUserDetailsService userDetailsService;
    private final RedirectUriValidator redirectUriValidator;
    private final AuthorizationCodeStore authorizationCodeStore;
    private final AuthorizationCodeGenerator authorizationCodeGenerator;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        HttpSession session = request.getSession();

        String redirectUri =
                (String) session.getAttribute(
                        SessionAttributes.OAUTH2_REDIRECT_URI
                );

        redirectUriValidator.validateRedirectUri(redirectUri);

        session.removeAttribute(SessionAttributes.OAUTH2_REDIRECT_URI);

        if (!(authentication instanceof OAuth2AuthenticationToken oauth2Authentication)) {
            throw new IllegalStateException(
                    "Unexpected authentication type: "
                            + authentication.getClass().getName()
            );
        }

        String registrationId = oauth2Authentication.getAuthorizedClientRegistrationId();

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof OAuth2User oauth2User)) {
            throw new IllegalStateException(
                    "Unexpected principal: " +
                            principal.getClass().getName()
            );
        }

        String subject = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");

        String userId = null;

        AuthorizationCode authorizationCode = null;

        if (Objects.equals(registrationId.toLowerCase(), "google")) {
            String givenName = oauth2User.getAttribute("given_name");
            String familyName = oauth2User.getAttribute("family_name");

            Boolean emailVerified = oauth2User.getAttribute("email_verified");

            if (subject == null || email == null || !Boolean.TRUE.equals(emailVerified)) {
                throw new IllegalStateException(
                        "Google did not provide a verified email identity"
                );
            }

            UserRepresentation keycloakUser = keycloakUserService.findExistingUser(email);

            if (keycloakUser == null) {
                userId = keycloakUserService.createUser(
                        subject,
                        email,
                        true,
                        givenName,
                        familyName,
                        false
                );

                passwordSetupLinkService.sendPasswordSetupLink(userId, email);
            }

            UserDetails user = userDetailsService.findOrCreate(
                    userId,
                    subject,
                    email
            );

            authorizationCode = AuthorizationCode.builder()
                    .registrationId(registrationId)
                    .username(user.getUsername())
                    .expiresAt(Instant.now().plusSeconds(60))
                    .build();
        } else {
            OAuth2AuthorizedClient authorizedClient =
                    authorizedClientService.loadAuthorizedClient(
                            registrationId,
                            oauth2Authentication.getName()
                    );

            if (authorizedClient != null) {
                OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

                String refreshTokenValue = refreshToken != null ? refreshToken.getTokenValue() : null;

                authorizationCode = AuthorizationCode.builder()
                        .registrationId(registrationId)
                        .refreshToken(refreshTokenValue)
                        .expiresAt(Instant.now().plusSeconds(60))
                        .build();
            }
        }

        String code = authorizationCodeGenerator.generate();

        authorizationCodeStore.save(
                code,
                authorizationCode
        );

        response.sendRedirect(redirectUri + "?code=" + code);
    }
}
