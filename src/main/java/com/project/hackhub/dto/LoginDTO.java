/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.dto;
 

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Oggetto di trasferimento dati (DTO) utilizzato per gestire le credenziali di accesso dell'utente.
 */
public record LoginDTO(
        /** Lo username dell'utente (obbligatorio e non vuoto). */
        @NotBlank String userName,
        /** La password dell'utente (con lunghezza minima di 8 caratteri). */
        @Size(min = 8) String password
) {
}