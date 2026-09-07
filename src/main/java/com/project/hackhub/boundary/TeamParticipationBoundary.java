/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.handler.TeamPartecipationHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Boundary per le azioni di partecipazione ai team.
 * Permette a un utente di abbandonare volontariamente un team.
 */
@RestController
@RequestMapping("/api/team-participation")
@AllArgsConstructor
public class TeamParticipationBoundary {

    private final TeamPartecipationHandler teamParticipationHandler;

    /**
     * Consente all'utente autenticato di lasciare un team.
     *
     * @param userId l'utente autenticato che abbandona il team
     * @param teamId l'ID del team da lasciare
     * @return una {@link ResponseEntity} con stato HTTP 200 OK
     * @throws IllegalArgumentException      se l'utente o il team non esistono
     * @throws UnsupportedOperationException se l'utente è il team leader o il team ha un solo membro
     */
    @PostMapping("/{teamId}/leave")
    public ResponseEntity<String> leaveTeam(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID teamId) {
        teamParticipationHandler.leaveTeam(userId, teamId);
        return ResponseEntity.ok("User has left the team");
    }
}