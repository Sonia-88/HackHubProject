/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.service;


import com.project.hackhub.model.user.User;
import com.project.hackhub.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

/**
 * Filtro di sicurezza personalizzato che intercettando ogni richiesta HTTP estrae e valida il token JWT dall'intestazione di autorizzazione.
 */
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final ServiceJwt serviceJwt;
    private final UserRepository userRepository;

    /**
     * Esegue la filtrazione della richiesta HTTP per validare il token JWT e impostare l'autenticazione nel contesto di sicurezza.
     *
     * @param request la richiesta HTTP in arrivo
     * @param response la risposta HTTP
     * @param chain la catena dei filtri
     * @throws ServletException in caso di errori di servlet
     * @throws IOException in caso di errori di I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token;
        UUID userId;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                userId = serviceJwt.extractUserId(token);
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userRepository.findById(userId).orElse(null);
                    if (user != null) {
                        serviceJwt.validateToken(token, user);
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getId(),
                                null, Collections.emptyList());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

}