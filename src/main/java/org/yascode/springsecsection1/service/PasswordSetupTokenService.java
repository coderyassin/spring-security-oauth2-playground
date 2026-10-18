package org.yascode.springsecsection1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yascode.springsecsection1.model.PasswordSetupToken;
import org.yascode.springsecsection1.repository.PasswordSetupTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Service
@RequiredArgsConstructor
public class PasswordSetupTokenService {

    private static final long AMOUNT_TO_ADD = 15;
    private static final TemporalUnit UNIT = ChronoUnit.MINUTES;

    private final PasswordSetupTokenRepository passwordSetupTokenRepository;

    @Transactional
    protected void saveToken(
            String userId,
            String tokenHash
    ) {
        PasswordSetupToken token = new PasswordSetupToken();

        token.setTokenHash(tokenHash);
        token.setUserId(userId);
        token.setExpiresAt(
                Instant.now().plus(AMOUNT_TO_ADD, UNIT)
        );

        passwordSetupTokenRepository.save(token);
    }
}
