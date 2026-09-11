/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.dto;


import com.project.hackhub.model.team.AidRequestType;

import java.util.UUID;

/**
 * Oggetto di trasferimento dati (DTO) che rappresenta i dettagli di una richiesta di aiuto o supporto.
 */
public record AidRequestDTO(
        /** La descrizione testuale della richiesta di aiuto. */
        String description,
        /** La tipologia della richiesta di aiuto {@code AidRequestType}. */
        AidRequestType type,
        /** L'identificativo univoco del team che effettua la richiesta. */
        UUID team
) {
}