/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.boundary;


import com.project.hackhub.dto.AuthResponse;
import com.project.hackhub.dto.LoginDTO;
import com.project.hackhub.handler.AuthenticationHandler;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST per le operazioni di autenticazione degli utenti.
 * Gestisce il login degli utenti e la generazione del token di autenticazione JWT.
 */
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/authentication")
public class AuthenticationBoundary {

    private final AuthenticationHandler authenticationHandler;

    /**
     * Autentica un utente con le credenziali fornite.
     *
     * @param dto DTO contenente le credenziali di login
     * @return ResponseEntity con la risposta di autenticazione inclusivo del token JWT
     */
    @PostMapping()
    public ResponseEntity<AuthResponse> authenticateUser(
            @Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authenticationHandler.authenticateUser(dto));
    }
}