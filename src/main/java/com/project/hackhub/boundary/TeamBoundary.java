/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;


import com.project.hackhub.handler.TeamHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Boundary per le operazioni relative ai team.
 * Consente la creazione, la modifica e la rimozione dei membri dei team.
 */
@RestController
@RequestMapping("/api/team")
@AllArgsConstructor
public class TeamBoundary {

    private final TeamHandler teamHandler;

    /**
     * Crea un nuovo team per un determinato hackathon.
     *
     * @param creatorId   l'utente autenticato che crea il team
     * @param hackathonId l'ID dell'hackathon
     * @param teamName    il nome del nuovo team
     * @return una {@link ResponseEntity} con stato HTTP 201 CREATED
     * @throws IllegalArgumentException      se il nome del team è vuoto o esiste già nell'hackathon
     * @throws UnsupportedOperationException se l'utente non ha i permessi per creare un team
     */
    @PostMapping("/{hackathonId}/creation")
    public ResponseEntity<Void> createTeam(
            @AuthenticationPrincipal UUID creatorId,
            @PathVariable UUID hackathonId,
            @RequestBody String teamName) {
        teamHandler.createTeam(creatorId, hackathonId, teamName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Aggiorna il nome di un team.
     *
     * @param editorId l'utente autenticato (deve essere team leader o organizzatore)
     * @param teamId   l'ID del team
     * @param newName  il nuovo nome del team
     * @return una {@link ResponseEntity} con conferma dell'avvenuto aggiornamento
     * @throws UnsupportedOperationException se l'utente non è né team leader né organizzatore
     */
    @PatchMapping("/{teamId}")
    public ResponseEntity<String> updateTeam(
            @AuthenticationPrincipal UUID editorId,
            @PathVariable UUID teamId,
            @RequestBody String newName) {
        teamHandler.updateTeam(editorId, teamId, newName);
        return ResponseEntity.ok("team has been successfully updated");
    }

    /**
     * Rimuove un membro da un team.
     *
     * @param requesterId l'utente autenticato (team leader o organizzatore)
     * @param teamId      l'ID del team
     * @param memberId    l'ID del membro da rimuovere
     * @return una {@link ResponseEntity} con stato HTTP 200 OK
     * @throws UnsupportedOperationException se l'utente manca di permessi o tenta di rimuovere il team leader
     * @throws IllegalStateException         se il membro non fa parte del team
     */
    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<String> removeMember(
            @AuthenticationPrincipal UUID requesterId,
            @PathVariable UUID teamId,
            @PathVariable UUID memberId) {
        teamHandler.removeMember(requesterId, teamId, memberId);
        return ResponseEntity.ok("Member removed from team");
    }
}