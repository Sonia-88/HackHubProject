/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.dto;


import java.util.UUID;

/**
 * Oggetto di trasferimento dati (DTO) per la sottomissione di un elaborato da parte di un team.
 */
public record SubmissionDTO(
        /** L'identificativo UUID del team che effettua la sottomissione. */
        UUID teamId,
        /** Il nome del file sottomesso. */
        String fileName) {}