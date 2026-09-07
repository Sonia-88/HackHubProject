/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.dto.PersonalDataDTO;
import com.project.hackhub.handler.AccountHandler;
import com.project.hackhub.model.user.PersonalData;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST per la gestione delle operazioni sugli account utente.
 * Espone gli endpoint per la registrazione e la gestione degli account.
 */
@AllArgsConstructor
@RestController
@Validated
@RequestMapping("/api/account")
public class AccountBoundary {

    private final AccountHandler accountHandler;

    /**
     * Crea un nuovo account utente con i dati personali forniti.
     *
     * @param personalDataDto DTO contenente le informazioni personali dell'utente
     * @return ResponseEntity con stato HTTP 201 Created
     */
    @PostMapping("/registration")
    public ResponseEntity<Void> createAccount(@Valid @RequestBody PersonalDataDTO personalDataDto) {
        accountHandler.createAccount(personalDataDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Aggiorna i dati personali dell'utente autenticato.
     *
     * @param userId l'identificativo UUID dell'utente autenticato
     * @param personalData i nuovi dati personali dell'utente
     * @return ResponseEntity con esito positivo dell'operazione
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateAccount(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody PersonalData personalData) {

        accountHandler.updateAccount(userId, personalData);
        return ResponseEntity.ok("updated successfully");
    }

    /**
     * Elimina l'account dell'utente autenticato.
     *
     * @param userId l'identificativo UUID dell'utente autenticato
     * @return ResponseEntity con conferma di avvenuta eliminazione
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal UUID userId) {
        accountHandler.deleteAccount(userId);
        return ResponseEntity.ok("Account deleted");
    }
}