/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.dto;
 

/**
 * Oggetto di trasferimento dati (DTO) utilizzato per la valutazione e il voto di una sottomissione.
 */
public record GradeDTO(
        /** Il voto numerico assegnato alla sottomissione. */
        float grade,
        /** La valutazione scritta o il giudizio testuale associato. */
        String writtenEvaluation) {
}