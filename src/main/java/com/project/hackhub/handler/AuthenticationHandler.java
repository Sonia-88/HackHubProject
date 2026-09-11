/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.handler;


import com.project.hackhub.dto.AuthResponse;
import com.project.hackhub.dto.LoginDTO;
import com.project.hackhub.model.user.User;
import com.project.hackhub.repository.UserRepository;
import com.project.hackhub.service.ServiceJwt;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Gestisce i processi di autenticazione degli utenti e la generazione dei token di accesso JWT.
 */
@Component
@AllArgsConstructor
public class AuthenticationHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceJwt serviceJwt;

    /**
     * Autentica un utente verificando le credenziali inserite e restituendo un token JWT valido.
     *
     * @param dto il DTO contenente le credenziali di login (username e password)
     * @return un oggetto {@code AuthResponse} contenente il token JWT generato
     * @throws IllegalArgumentException se lo username non è valido o la password non corrisponde
     */
    @Transactional
    public AuthResponse authenticateUser(LoginDTO dto) {
        User user = userRepository.findByPersonalData_UserName(dto.userName())
                .orElseThrow(() -> new IllegalArgumentException("userName invalid"));
        if (!passwordEncoder.matches(dto.password(), user.getPasswordHash()))
            throw new IllegalArgumentException("Password not valid");
        String token = serviceJwt.generateToken(user);
        return new AuthResponse(token, "Bearer");
    }
}