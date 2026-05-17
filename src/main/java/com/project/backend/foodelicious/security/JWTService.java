package com.project.backend.foodelicious.security;


import com.project.backend.foodelicious.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService {

    // How long access token lives — 10 minutes
    private static final long ACCESS_TOKEN_EXPIRY = 1000 * 60 * 10;
    // How long refresh token lives — 6 months
    private static final long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 60 * 24 * 30 * 6;
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return generateToken(user, ACCESS_TOKEN_EXPIRY);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_TOKEN_EXPIRY);
    }

    private String generateToken(User user, long expiry) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(getSecretKey())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}