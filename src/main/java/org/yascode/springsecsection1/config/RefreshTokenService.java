package org.yascode.springsecsection1.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yascode.springsecsection1.config.exception.InvalidRefreshTokenException;
import org.yascode.springsecsection1.model.RefreshToken;
import org.yascode.springsecsection1.model.User;
import org.yascode.springsecsection1.repository.RefreshTokenRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final Duration TTL = Duration.ofDays(30);
    private static final Duration GRACE_PERIOD = Duration.ofSeconds(10);

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtHelper jwtHelper;

    public record IssuedToken(String rawToken, Instant expiresAt) {}
    public record TokenPair(String accessToken, IssuedToken refreshToken) {}

    @Transactional
    public IssuedToken issueForLogin(User user) {
        return create(user, UUID.randomUUID());
    }

    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public TokenPair rotate(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidRefreshTokenException("Missing refresh token");
        }

        String hash = TokenHasher.hash(rawToken);

        RefreshToken current = refreshTokenRepository.findByTokenHashForUpdate(hash)
                .orElseThrow(() -> new InvalidRefreshTokenException("Unknown refresh token"));

        Instant now = Instant.now();

        if (current.isRevoked()) {
            handleReuse(current, now);
            throw new InvalidRefreshTokenException("Refresh token revoked");
        }

        if (current.isExpired()) {
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        User user = current.getUser();

        if (user == null) {
            throw new InvalidRefreshTokenException("Unknown user");
        }

        if(!user.isAccountNonExpired()) {
            throw new InvalidRefreshTokenException("User account expired");
        }

        if(!user.isAccountNonLocked()) {
            throw new InvalidRefreshTokenException("User account locked");
        }

        if(!user.isCredentialsNonExpired()) {
            throw new InvalidRefreshTokenException("User credentials expired");
        }

        if(!user.isEnabled()) {
            throw new InvalidRefreshTokenException("User account disabled");
        }

        IssuedToken next = create(user, current.getFamilyId());
        current.revoke(TokenHasher.hash(next.rawToken()));
        current.setLastUsedAt(now);

        String accessToken = jwtHelper.generateAccessToken(user);
        return new TokenPair(accessToken, next);
    }

    @Transactional
    public void logout(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        refreshTokenRepository.findByTokenHashForUpdate(TokenHasher.hash(rawToken))
                .ifPresent(t -> refreshTokenRepository.revokeFamily(t.getFamilyId(), Instant.now()));
    }

    private void handleReuse(RefreshToken token, Instant now) {
        // Grace period: a token that has just been replaced can be returned by a second tab.
        // It is rejected without penalizing the entire family.
        boolean justRotated = token.getReplacedByHash() != null
                && token.getRevokedAt() != null
                && token.getRevokedAt().isAfter(now.minus(GRACE_PERIOD));

        if (!justRotated) {
            refreshTokenRepository.revokeFamily(token.getFamilyId(), now);
        }
    }

    public IssuedToken create(User user) {
        return create(user, UUID.randomUUID());
    }

    public IssuedToken create(User user, UUID familyId) {
        String raw = TokenHasher.generateRefreshToken();
        Instant expiresAt = Instant.now().plus(TTL);

        refreshTokenRepository.save(RefreshToken.builder()
                .tokenHash(TokenHasher.hash(raw))
                .user(user)
                .familyId(familyId)
                .expiresAt(expiresAt)
                .build());

        return new IssuedToken(raw, expiresAt);
    }
}
