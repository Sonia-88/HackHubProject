/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.dto;
 

import java.util.UUID;

/**
 * Oggetto di trasferimento dati (DTO) per gestire la richiesta di modifica del ruolo di un membro dello staff.
 */
public record UpdateStaffRequestDTO(
        /** L'identificativo UUID dell'utente di cui si desidera modificare il ruolo. */
        UUID toChange,
        /** La stringa rappresentante il nuovo ruolo da assegnare. */
        String role
) {

}