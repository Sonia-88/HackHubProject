/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.team;
 

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Rappresenta un template di file o documento allegato (es. sottomissioni o certificati).
 */
@NoArgsConstructor
@Getter @Setter
@Embeddable
public class FileTemplate {
    /** Il nome del file. */
    private String fileName;
}