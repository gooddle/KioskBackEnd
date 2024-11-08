package com.kotlin.foodkiosk.infra.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Configuration
public class JwtPlugin {

    @Value("${auth.jwt.issuer}")
    private String issuer;

    @Value("${auth.jwt.secret}")
    private String secret;

    @Value("${auth.jwt.accessTokenExpirationHour}")
    private long accessTokenExpirationHour;

    public Optional<Jws<Claims>> validateToken(String jwt) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String generateAccessToken(String subject, String role, String userName) {
        return generateToken(subject, role, userName, Duration.ofHours(accessTokenExpirationHour));
    }

    private String generateToken(String subject, String role, String userName, Duration expirationPeriod) {
        Claims claims = Jwts.claims()
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(expirationPeriod)));

        claims.put("role", role);
        claims.put("userName", userName);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }
}
