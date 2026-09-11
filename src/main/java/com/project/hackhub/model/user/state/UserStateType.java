/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.user.state;
 

/**
 * Enumerazione che elenca tutti i possibili tipi di stato o ruolo che un utente
 * può assumere nell'ambito di un hackathon.
 */
public enum UserStateType  {
    /** Ruolo di coordinatore dell'evento. */
    COORDINATOR,
    /** Ruolo di mentor. */
    MENTOR,
    /** Ruolo di giudice. */
    JUDGE,
    /** Ruolo di semplice membro di un team. */
    TEAM_MEMBER,
    /** Ruolo di leader di un team. */
    TEAM_LEADER,
    /** Stato predefinito senza ruoli specifici. */
    DEFAULT_STATE
}