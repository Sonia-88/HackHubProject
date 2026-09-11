/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.handler.RequestOrganizerPermitHandler;
import com.project.hackhub.model.team.FileTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller REST per la gestione delle richieste di permessi da organizzatore.
 * Gestisce le richieste degli utenti per ottenere i privilegi di organizzazione degli hackathon.
 */
@RestController
@RequestMapping("/api/organizer/permit")
@RequiredArgsConstructor
public class RequestOrganizerPermitBoundary {

    private final RequestOrganizerPermitHandler requestOrganizerPermitHandler;

    /**
     * Richiede i permessi per organizzare gli hackathon.
     *
     * @param user UUID dell'utente autenticato che richiede il permesso
     * @param f template di file con i dettagli di qualifica da organizzatore
     * @return messaggio di conferma
     */
    @PatchMapping("/request")
    public ResponseEntity<String> requestPermission(
            @AuthenticationPrincipal UUID user,
            @RequestBody FileTemplate f) {

        requestOrganizerPermitHandler.requestPermission(user, f);
        return ResponseEntity.ok("Permission to organize hackathon granted!");
    }
}