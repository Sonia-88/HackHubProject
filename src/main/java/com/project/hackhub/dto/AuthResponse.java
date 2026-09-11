/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.dto;
 

/**
 * Oggetto di trasferimento dati (DTO) contenente la risposta di autenticazione dell'utente.
 */
public record AuthResponse(
        /** Il token di accesso JWT generato. */
        String token,
        /** Il tipo di token (es. Bearer). */
        String type
) {

}