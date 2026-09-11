/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.exceptions;
 

/**
 * Eccezione personalizzata per gestire la non disponibilità di un utente
 * in relazione a una specifica prenotazione.
 *
 * @author Cosmina Androne
 */
public class UserNotAvailableException extends RuntimeException {

    /**
     * Costruisce una nuova eccezione {@code UserNotAvailableException} con il messaggio specificato.
     *
     * @param message il messaggio descrittivo dell'errore
     */
    public UserNotAvailableException(String message) {
        super(message);
    }
}