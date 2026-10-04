package org.yascode.springsecsection1.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationProvider;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.yascode.springsecsection1.config.security.web.access.AuthorizationEventsProperties;
import org.yascode.springsecsection1.config.security.web.access.CustomAuthorizationEventPublisher;
import org.yascode.springsecsection1.config.security.web.access.CustomBasicAccessDeniedHandler;
import org.yascode.springsecsection1.config.security.web.authentication.CustomBasicAuthenticationEntryPoint;
import org.yascode.springsecsection1.config.security.web.authentication.GlobalAuthenticationEntryPoint;
import org.yascode.springsecsection1.config.security.web.authentication.OAuth2AuthenticationSuccessHandler;
import org.yascode.springsecsection1.config.security.web.cors.CustomCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityFilterChainConfiguration {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            OneTimeTokenHeaderFilter oneTimeTokenHeaderFilter,
            DaoAuthenticationProvider daoAuthenticationProvider,
//            SessionRegistry sessionRegistry,
            CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint,
            GlobalAuthenticationEntryPoint globalAuthenticationEntryPoint,
            @Qualifier("OAuth2AuthenticationSuccessHandler") AuthenticationSuccessHandler authenticationSuccessHandler,
            CustomBasicAccessDeniedHandler customBasicAccessDeniedHandler,
            CustomCorsConfigurationSource customCorsConfigurationSource
    ) {

        http.redirectToHttps(Customizer.withDefaults());

        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers("/auth/**","/oauth2/**", "/login/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/ott/generate", "/login/ott").permitAll()
                .requestMatchers(HttpMethod.GET, "/welcome").hasAuthority("UPDATE")
                .anyRequest().authenticated());
        http.authenticationProvider(daoAuthenticationProvider);

        http.oauth2Login(oauth -> oauth
                .successHandler(authenticationSuccessHandler)
        );

//        http.sessionManagement(sessionManagement ->
//                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.ALWAYS).
//                maximumSessions(1).
//                maxSessionsPreventsLogin(true).
//                sessionRegistry(sessionRegistry)
//                sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
//        );

        http.addFilterBefore(
                oneTimeTokenHeaderFilter,
                BasicAuthenticationFilter.class
        );

        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(httpBasic ->
                httpBasic.authenticationEntryPoint(customBasicAuthenticationEntryPoint));

        http.exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(globalAuthenticationEntryPoint).
                accessDeniedHandler(customBasicAccessDeniedHandler));

        http.cors(cors -> cors.configurationSource(customCorsConfigurationSource));

        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                PathRequest.toH2Console(),
                PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/ott/generate"),
                PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/ott")
        ));

        http.csrf(AbstractHttpConfigurer::disable);

        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public OneTimeTokenAuthenticationProvider oneTimeTokenAuthenticationProvider(
            OneTimeTokenService oneTimeTokenService, UserDetailsService userDetailsService) {
        return new OneTimeTokenAuthenticationProvider(oneTimeTokenService, userDetailsService);
    }

    @Bean
    public OneTimeTokenHeaderFilter oneTimeTokenHeaderFilter(OneTimeTokenAuthenticationProvider oneTimeTokenAuthenticationProvider) {
        return new OneTimeTokenHeaderFilter(oneTimeTokenAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher,
            AuthorizationEventsProperties properties
    ) {
        CustomAuthorizationEventPublisher authorizationEventPublisher = new CustomAuthorizationEventPublisher(applicationEventPublisher);
        authorizationEventPublisher.setShouldPublishResult(result -> {
            return result.isGranted() ? properties.isPublishGranted() : properties.isPublishDenied();
        });
        return authorizationEventPublisher;
    }

//    @Bean
//    public SessionRegistry sessionRegistry() {
//        return new SessionRegistryImpl();
//    }
}
