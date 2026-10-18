package org.yascode.springsecsection1.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yascode.springsecsection1.config.exception.InvalidPasswordSetupTokenException;
import org.yascode.springsecsection1.model.PasswordSetupToken;
import org.yascode.springsecsection1.repository.PasswordSetupTokenRepository;
import org.yascode.springsecsection1.utils.HashUtils;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PasswordSetupService {

    private final UserService userService;

    private final PasswordSetupTokenRepository tokenRepository;
    private final Keycloak keycloak;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Transactional
    public void confirmPassword(String code, String password) {
        String tokenHash = HashUtils.hashCode(code);

        PasswordSetupToken token = tokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(InvalidPasswordSetupTokenException::new);

        Instant now = Instant.now();

        if (token.getUsedAt() != null
                || !token.getExpiresAt().isAfter(now)) {
            throw new InvalidPasswordSetupTokenException();
        }

        var userResource = keycloak
                .realm(realm)
                .users()
                .get(token.getUserId());

        UserRepresentation user = userResource.toRepresentation();

        if (user == null) {
            throw new InvalidPasswordSetupTokenException();
        }

        CredentialRepresentation credential =
                new CredentialRepresentation();

        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        userResource.resetPassword(credential);

        user.setEnabled(true);
        userResource.update(user);

        userService.activateByUserId(token.getUserId());

        token.setUsedAt(now);
        tokenRepository.save(token);
    }
}
