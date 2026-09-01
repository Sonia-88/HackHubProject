/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.state;
 

/**
 * Enumerazione che elenca tutti i possibili stati in cui un Hackathon può trovarsi
 * durante il suo ciclo di vita.
 */
public enum HackathonStateType {
    /** Fase di iscrizione aperta. */
    SUBSCRIPTION_PHASE,
    /** Evento in corso di svolgimento. */
    ONGOING,
    /** Fase di valutazione dei progetti da parte dei giudici. */
    APPRAISAL,
    /** Evento concluso con proclamazione del vincitore. */
    CONCLUDED
}