package org.yascode.springsecsection1.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public final class JwtHelper {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;

    public JwtHelper(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration-ms}") long accessTokenExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public String generateAccessToken(UserDetails userDetails) {
        Instant now = Instant.now();

        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuer("yascode")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenExpirationMs)))
                .claim("type", "access")
                .claim("authorities", authorities)
                .signWith(secretKey)
                .compact();
    }
}
