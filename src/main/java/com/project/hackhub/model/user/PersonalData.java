/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.model.user;


import com.project.hackhub.model.hackathon.Location;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Oggetto incorporabile (Embeddable) che raccoglie i dati anagrafici e di contatto di un utente.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PersonalData {

    /** Il nome dell'utente. */
    private String userName;
    /** Il cognome dell'utente. */
    private String userSurname;
    /** Il codice fiscale dell'utente. */
    private String fiscalCode;
    /** L'indirizzo o location di residenza. */
    Location address;
    /** L'indirizzo email dell'utente. */
    private String email;

}