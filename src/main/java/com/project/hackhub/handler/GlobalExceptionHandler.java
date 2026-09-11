/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.handler;
 

import com.project.hackhub.exceptions.UserNotAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestore globale delle eccezioni per intercettare e convertire gli errori applicativi
 * in risposte HTTP strutturate e coerenti.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestisce l'eccezione {@code UserNotAvailableException} restituendo uno stato HTTP CONFLICT.
     *
     * @param ex l'eccezione catturata
     * @return un {@code ResponseEntity} con il messaggio di errore e codice di stato 409
     */
    @ExceptionHandler(UserNotAvailableException.class)
    public ResponseEntity<String> handleConflict(UserNotAvailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    /**
     * Gestisce l'eccezione {@code IllegalArgumentException} restituendo uno stato HTTP BAD_REQUEST.
     *
     * @param ex l'eccezione catturata
     * @return un {@code ResponseEntity} con il messaggio di errore e codice di stato 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Gestisce l'eccezione {@code IllegalStateException} restituendo uno stato HTTP BAD_REQUEST.
     *
     * @param ex l'eccezione catturata
     * @return un {@code ResponseEntity} con il messaggio di errore e codice di stato 400
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Gestisce l'eccezione {@code UnsupportedOperationException} restituendo uno stato HTTP BAD_REQUEST.
     *
     * @param ex l'eccezione catturata
     * @return un {@code ResponseEntity} con il messaggio di errore e codice di stato 400
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<String> handleUnsupportedOperation(UnsupportedOperationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}