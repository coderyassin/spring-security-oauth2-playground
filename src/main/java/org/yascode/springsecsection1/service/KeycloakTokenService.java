package org.yascode.springsecsection1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakTokenService {

    private final RestClient.Builder restClientBuilder;

    @Value("${keycloak.auth.base-url}")
    private String keycloakUrl;

    @Value("${keycloak.auth.realm}")
    private String realm;

    public Map<String, Object> refreshAccessToken(
            String clientId,
            String clientSecret,
            String refreshToken
    ) {
        RestClient client = restClientBuilder.build();

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        return client.post()
                .uri(keycloakUrl
                        + "/realms/" + realm
                        + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}


