package org.yascode.springsecsection1.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationProvider;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.util.StringUtils;

public class OneTimeTokenHeaderFilter extends AuthenticationFilter {

    private static final String HEADER_NAME = "token";

    public OneTimeTokenHeaderFilter(OneTimeTokenAuthenticationProvider provider) {
        super(createAuthenticationManager(provider), createAuthenticationConverter());

        this.setRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/ott"));
        this.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
        this.setSuccessHandler((request, response, authentication) -> {
        });
    }

    private static AuthenticationManager createAuthenticationManager(OneTimeTokenAuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    private static AuthenticationConverter createAuthenticationConverter() {
        return (HttpServletRequest request) -> {
            String token = request.getHeader(HEADER_NAME);

            return StringUtils.hasText(token)
                    ? new OneTimeTokenAuthenticationToken(token)
                    : null;
        };
    }
}
