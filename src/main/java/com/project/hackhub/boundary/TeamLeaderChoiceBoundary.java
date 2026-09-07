/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.handler.TeamLeaderChoiceHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST per la gestione del cambio del team leader.
 * Gestisce la selezione e l'assegnazione di un nuovo team leader.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teamLeader")
public class TeamLeaderChoiceBoundary {

    private final TeamLeaderChoiceHandler teamLeaderChoiceHandler;

    /**
     * Modifica il team leader nominandone un nuovo membro.
     *
     * @param newLeader UUID dell'utente che diventerà il nuovo team leader
     * @param oldLeader UUID dell'attuale team leader autenticato
     * @param t UUID del team
     * @return messaggio di conferma con le informazioni del nuovo leader
     */
    @PatchMapping("/{t}/choice")
    public ResponseEntity<String> chooseNewTeamLeader(
            @RequestBody UUID newLeader,
            @AuthenticationPrincipal UUID oldLeader,
            @PathVariable UUID t) {

        teamLeaderChoiceHandler.chooseNewTeamLeader(newLeader, oldLeader, t);
        return ResponseEntity.ok("New team leader: " + newLeader);
    }
}