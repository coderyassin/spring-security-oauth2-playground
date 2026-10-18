package org.yascode.springsecsection1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yascode.springsecsection1.config.exception.EmailAlreadyRegisteredException;
import org.yascode.springsecsection1.controller.request.RegisterRequest;
import org.yascode.springsecsection1.factory.UserFactory;
import org.yascode.springsecsection1.model.User;
import org.yascode.springsecsection1.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final KeycloakUserService keycloakUserService;
    private final UserRepository userRepository;
    private final PasswordSetupLinkService passwordSetupLinkService;

    private final UserFactory userFactory;

    @Transactional
    public void register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        String keycloakUserId =
                keycloakUserService.createPendingUser(
                        email,
                        request.firstName().trim(),
                        request.lastName().trim()
                );

        User user = userFactory.createDisabled(keycloakUserId, email, null, email);

        userRepository.save(user);

        passwordSetupLinkService.sendPasswordSetupLink(
                keycloakUserId,
                email
        );
    }
}
