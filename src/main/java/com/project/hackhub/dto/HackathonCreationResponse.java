/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.dto;
 

/**
 * Oggetto di trasferimento dati (DTO) che restituisce l'esito del processo di creazione di un hackathon.
 */
public record HackathonCreationResponse(
        /** Valore booleano che indica se la creazione è andata a buon fine o è stata sospesa. */
        boolean created,
        /** Messaggio descrittivo associato all'esito della creazione. */
        String message
) {}