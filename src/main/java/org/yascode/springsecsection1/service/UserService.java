package org.yascode.springsecsection1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yascode.springsecsection1.model.User;
import org.yascode.springsecsection1.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void activateByUserId(String keycloakUserId) {
        User user = userRepository
                .findByUserId(keycloakUserId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Application user not found"
                        ));

        user.setEnabled(true);
    }

    @PostAuthorize("@userSecurity.isOwner(returnObject, authentication)")
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );
    }

    public User getById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );
    }
}
