/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.dto;
 

import java.util.UUID;

/**
 * Oggetto di trasferimento dati (DTO) che racchiude le informazioni relative a un'infrazione commessa da un team.
 */
public record InfractionDTO (
        /** La descrizione dettagliata dell'infrazione. */
        String description,
        /** La tipologia o categoria dell'infrazione. */
        String type,
        /** L'identificativo UUID del team coinvolto. */
        UUID team
){ }