package org.yascode.springsecsection1.config.security.web.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.yascode.springsecsection1.config.*;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final CustomUserDetailsService userDetailsService;
    private final RedirectUriValidator redirectUriValidator;
    private final AuthorizationCodeStore authorizationCodeStore;
    private final AuthorizationCodeGenerator authorizationCodeGenerator;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        HttpSession session = request.getSession();

        String redirectUri =
                (String) session.getAttribute(
                        SessionAttributes.OAUTH2_REDIRECT_URI
                );

        redirectUriValidator.validateRedirectUri(redirectUri);

        session.removeAttribute("OAUTH2_REDIRECT_URI");

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof OAuth2User oauth2User)) {
            throw new IllegalStateException("Unexpected principal : " + authentication.getPrincipal());
        }

        String subject = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");

        UserDetails user = userDetailsService.findOrCreate(
                subject,
                email
        );

        String code = authorizationCodeGenerator.generate();

        AuthorizationCode authorizationCode =
                new AuthorizationCode(
                        user.getUsername(),
                        Instant.now().plusSeconds(60)
                );

        authorizationCodeStore.save(
                code,
                authorizationCode
        );

        response.sendRedirect(redirectUri + "?code=" + code);
    }
}

/**
 *
 *  1. front send a request to http://localhost:8080/auth/google
 *  2. BBF redirect to google
 *  3. google redirects back to http://localhost:8080/authorized with code
 *  4. BFF receives code and exchanges it for an access token
 *  5. BFF sends a request to the user info endpoint to get user details
 *  6. BFF creates or updates the user in the database
 *  7. BFF generates a JWT and sends it back to the front
 *  8. Front stores the JWT and redirects to the home page
 *
  */
