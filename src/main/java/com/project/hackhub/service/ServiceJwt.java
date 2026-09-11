/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.service;
 

import com.project.hackhub.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Servizio di utilità per la generazione, l'estrazione e la validazione dei token JWT.
 */
@Service
public class ServiceJwt {

    @Value("${app.jwt.secret}")
    private String jwtSecret;
    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Helper method to get the key
     *
     * @return the secret key to sign Jwt token, obtained from string jwtSecret
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a Jwt token for a user trying to authenticate to the platform
     *
     * @param user the user trying to authenticate to the platform
     * @return a Jwt token as string, containing the id of the user as subject
     */
    public String generateToken(User user) {
        if (user == null) throw new IllegalArgumentException("The user cannot be null");

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the user id from a given token
     * @param token the given token
     * @return the {@link UUID} of the user contained in the token
     */
    public UUID extractUserId(String token) {

        if (token == null) throw new IllegalArgumentException("Token cannot be null");

        String id =  Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return UUID.fromString(id);
    }

    /**
     * Verifies that a token is valid
     *
     * @param token  the token to validate
     * @param user the user performing the action
     * @throws IllegalArgumentException if token or user are null
     * @throws RuntimeException if the token is not valid, expired or not associated to the given user
     */
    public void validateToken(String token, User user) {
        if (token == null || user == null)
            throw new IllegalArgumentException("Token or User cannot be null");

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (!claims.getSubject().equals(user.getId().toString()))
                throw new JwtException("Token does not correspond to User");
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Jwt Token expired or not valid");
        }
    }
}