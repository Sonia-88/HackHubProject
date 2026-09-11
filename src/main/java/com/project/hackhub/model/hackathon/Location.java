/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon;
 

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Oggetto incorporabile (Embeddable) che rappresenta i dati geografici e di indirizzo di una sede.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class Location {

    /** Il nome della località o struttura. */
    private String name;
    /** La provincia di appartenenza. */
    private String province;
    /** Il codice di avviamento postale (CAP). */
    private int cap;
    /** L'indirizzo fisico. */
    private String address;
}