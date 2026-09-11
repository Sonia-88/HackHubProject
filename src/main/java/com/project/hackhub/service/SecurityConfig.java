/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.service;
 

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Classe di configurazione per Spring Security, definisce le regole di autorizzazione,
 * la catena dei filtri e il meccanismo di codifica delle password.
 */
@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final JwtFilter filter;

    /**
     * Configura la catena dei filtri di sicurezza HTTP.
     *
     * @param http l'oggetto {@link HttpSecurity} da configurare
     * @return il {@link SecurityFilterChain} configurato
     * @throws Exception in caso di errori di configurazione
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/account/registration").permitAll()
                        .requestMatchers("/api/authentication").permitAll()
                        .requestMatchers("/api/info/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // --- AGGIUNTE PER SWAGGER ---
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()

                        .requestMatchers("/error").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                .build();
    }

    /**
     * Definisce il bean per la cifratura delle password basato su BCrypt.
     *
     * @return un'istanza di {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}