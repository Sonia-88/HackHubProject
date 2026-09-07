/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.handler.ParticipationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller REST per la gestione della partecipazione ai team.
 * Gestisce le operazioni di iscrizione e disiscrizione dei team.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teamPartecipation")
public class ParticipationBoundary {

    private final ParticipationHandler participationHandler;

    /**
     * Disiscrive un utente o un team dall'hackathon.
     *
     * @param team UUID del team
     * @param user UUID dell'utente autenticato che effettua la disiscrizione
     * @return messaggio di conferma
     */
    @DeleteMapping("/unsubscribeTeam/{team}")
    public ResponseEntity<String> unsubscribeTeam(
            @PathVariable UUID team,
            @AuthenticationPrincipal UUID user) {

        participationHandler.unsubscribeTeam(team, user);
        return ResponseEntity.ok("Team successfully unsubscribed");
    }
}