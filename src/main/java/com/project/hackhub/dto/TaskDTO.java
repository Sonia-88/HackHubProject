/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.dto;


import com.project.hackhub.model.team.FileTemplate;

/**
 * Oggetto di trasferimento dati (DTO) per la definizione e l'inserimento di un task all'interno di un hackathon.
 */
public record TaskDTO(
        /** Il titolo del task. */
        String title,
        /** La descrizione dettagliata del task. */
        String description,
        /** Il template di file associato al task. */
        FileTemplate template
) {
}