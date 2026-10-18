package org.yascode.springsecsection1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.yascode.springsecsection1.config.exception.InvalidCredentialsException;
import org.yascode.springsecsection1.config.exception.KeycloakAuthenticationException;
import org.yascode.springsecsection1.controller.response.TokenResponse;

@Service
public class KeycloakLoginService {

    private final RestClient restClient;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    public KeycloakLoginService(
            RestClient.Builder builder,
            @Value("${keycloak.auth.base-url}") String baseUrl,
            @Value("${keycloak.auth.realm}") String realm,
            @Value("${keycloak.auth.client-id}") String clientId,
            @Value("${keycloak.auth.client-secret}") String clientSecret
    ) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();

        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public TokenResponse authenticate(
            String email,
            String password
    ) {
        MultiValueMap<String, String> form =
                new LinkedMultiValueMap<>();

        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", email);
        form.add("password", password);
        form.add("scope", "openid");

        try {
            TokenResponse result = restClient.post()
                    .uri("/realms/{realm}/protocol/openid-connect/token",
                            realm)
                    .contentType(
                            MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(form)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            (request, response) -> {
                                if (response.getStatusCode().value()
                                        == 400
                                        || response.getStatusCode().value()
                                        == 401) {
                                    throw new InvalidCredentialsException();
                                }

                                throw new KeycloakAuthenticationException();
                            }
                    )
                    .body(TokenResponse.class);

            if (result == null || result.accessToken() == null) {
                throw new KeycloakAuthenticationException();
            }

            return result;

        } catch (InvalidCredentialsException
                 | KeycloakAuthenticationException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new KeycloakAuthenticationException();
        }
    }
}
