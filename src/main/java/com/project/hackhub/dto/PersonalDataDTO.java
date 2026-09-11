/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.dto;


import com.project.hackhub.model.hackathon.Location;

/**
 * Oggetto di trasferimento dati (DTO) contenente i dati anagrafici e di registrazione di un nuovo utente.
 */
public record PersonalDataDTO(
        /** Il nome dell'utente. */
        String userName,
        /** Il cognome dell'utente. */
        String userSurname,
        /** Il codice fiscale dell'utente. */
        String fiscalCode,
        /** L'indirizzo o location di residenza dell'utente. */
        Location address,
        /** L'indirizzo email dell'utente. */
        String email,
        /** La password associata all'account. */
        String password
){ }