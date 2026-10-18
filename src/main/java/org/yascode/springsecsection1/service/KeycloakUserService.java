package org.yascode.springsecsection1.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.yascode.springsecsection1.config.exception.EmailAlreadyRegisteredException;
import org.yascode.springsecsection1.config.security.oauth2.KeycloakAdminProperties;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private final Keycloak keycloak;
    private final KeycloakAdminProperties properties;

    public List<UserRepresentation> findByEmail(String email) {
        return keycloak.realm(properties.realm())
                .users()
                .searchByEmail(email.trim(), true);
    }

    public UserRepresentation findExistingUser(String email) {
        List<UserRepresentation> users = findByEmail(email);

        if (users.isEmpty()) {
            return null;
        }

        if (users.size() > 1) {
            throw new IllegalStateException(
                    "Several Keycloak users match the same email"
            );
        }

        return users.getFirst();
    }

    public String createUser(
            String googleSub,
            String email,
            Boolean emailVerified,
            String firstName,
            String lastName,
            Boolean enabled
    ) {
        UserRepresentation user = new UserRepresentation();

        user.setUsername(email.trim().toLowerCase());
        user.setEmail(email.trim());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailVerified(emailVerified);
        user.setEnabled(enabled);

        user.setAttributes(Map.of(
                "googleSub", List.of(googleSub),
                "registrationSource", List.of("google")
        ));

        try (Response response = keycloak.realm(properties.realm())
                .users()
                .create(user)) {

            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new IllegalStateException(
                        "Keycloak user creation failed with HTTP "
                                + response.getStatus()
                );
            }

            return extractUserIdFromLocation(response.getLocation());
        }
    }

    public String createPendingUser(
            String email,
            String firstName,
            String lastName
    ) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(false);
        user.setEmailVerified(false);

        try (Response response = keycloak
                .realm(properties.realm())
                .users()
                .create(user)) {

            if (response.getStatus() != 201) {
                if (response.getStatus() == 409) {
                    throw new EmailAlreadyRegisteredException();
                }

                throw new IllegalStateException(
                        "Unable to create the Keycloak account"
                );
            }

            String location = response.getHeaderString("Location");

            if (location == null || location.isBlank()) {
                throw new IllegalStateException(
                        "Keycloak did not return the user ID."
                );
            }

            return location.substring(
                    location.lastIndexOf('/') + 1
            );
        }
    }

    private String extractUserIdFromLocation(URI location) {
        if (location == null) {
            throw new IllegalStateException(
                    "Keycloak created the user but returned no Location"
            );
        }

        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

}
