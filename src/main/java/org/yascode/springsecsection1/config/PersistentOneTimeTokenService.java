package org.yascode.springsecsection1.config;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yascode.springsecsection1.model.TokenType;
import org.yascode.springsecsection1.repository.OneTimeTokenRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class PersistentOneTimeTokenService implements OneTimeTokenService {

    private final OneTimeTokenRepository oneTimeTokenRepository;

    public PersistentOneTimeTokenService(OneTimeTokenRepository oneTimeTokenRepository) {
        this.oneTimeTokenRepository = oneTimeTokenRepository;
    }

    @Override
    @Transactional
    public OneTimeToken generate(GenerateOneTimeTokenRequest request) {
        Instant now = Instant.now();
        oneTimeTokenRepository.deleteByExpiresAtBefore(toLocalDateTime(now));

        org.yascode.springsecsection1.model.OneTimeToken entity = org.yascode.springsecsection1.model.OneTimeToken.builder()
                .tokenValue(UUID.randomUUID().toString())
                .username(request.getUsername())
                .issuedAt(toLocalDateTime(now))
                .expiresAt(toLocalDateTime(now.plus(request.getExpiresIn())))
                .used(false)
                .attempts(0)
                .tokenType(TokenType.PASSWORDLESS_LOGIN)
                .build();

        return toSecurityToken(oneTimeTokenRepository.save(entity));
    }

    @Override
    @Transactional
    public @Nullable OneTimeToken consume(OneTimeTokenAuthenticationToken authenticationToken) {
        Instant now = Instant.now();
        return oneTimeTokenRepository.findByTokenValueAndUsedFalse(authenticationToken.getTokenValue())
                .map(entity -> consumeIfValid(entity, now))
                .orElse(null);
    }

    private @Nullable OneTimeToken consumeIfValid(org.yascode.springsecsection1.model.OneTimeToken entity, Instant now) {
        entity.setAttempts(entity.getAttempts() + 1);

        if (!entity.getExpiresAt().isAfter(toLocalDateTime(now))) {
            entity.setUsed(true);
            return null;
        }

        entity.setUsed(true);
        return toSecurityToken(entity);
    }

    private static OneTimeToken toSecurityToken(org.yascode.springsecsection1.model.OneTimeToken entity) {
        return new StoredOneTimeToken(
                entity.getTokenValue(),
                entity.getUsername(),
                toInstant(entity.getExpiresAt())
        );
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static Instant toInstant(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC);
    }
}
