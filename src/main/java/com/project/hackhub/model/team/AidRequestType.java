/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.team;
 

/**
 * Enumerazione che definisce le diverse tipologie di richieste di supporto.
 */
public enum AidRequestType {
    /** Proposta di chiamata. */
    CALL_PROPOSAL,
    /** Richiesta urgente. */
    URGENT,
    /** Malfunzionamento del sistema. */
    SYSTEM_MALFUNCTION,
    /** Richiesta accettata. */
    ACCEPTED,
    /** Richiesta rifiutata. */
    REFUSED
}