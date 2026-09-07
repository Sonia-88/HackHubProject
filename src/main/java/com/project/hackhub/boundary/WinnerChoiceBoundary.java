/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.handler.WinnerChoiceHandler;
import com.project.hackhub.model.team.Team;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per la gestione della proclamazione del vincitore e il recupero dei team.
 */
@RestController
@RequestMapping("/api/winner")
public class WinnerChoiceBoundary {
    private final WinnerChoiceHandler winnerChoiceHandler;

    public WinnerChoiceBoundary(WinnerChoiceHandler winnerChoiceHandler){
        this.winnerChoiceHandler = winnerChoiceHandler;
    }

    /**
     * Proclama il team vincitore per un determinato hackathon.
     *
     * @param hackathonId l'identificatore UUID dell'hackathon
     * @param organizerId l'identificatore UUID dell'organizzatore autenticato
     * @param teamId l'identificatore UUID del team proclamato vincitore
     * @return ResponseEntity con il nome del team vincitore
     */
    @PatchMapping("/{hackathonId}")
    public ResponseEntity<String> proclaimWinner(
            @PathVariable UUID hackathonId,
            @AuthenticationPrincipal UUID organizerId,
            @RequestBody UUID teamId) {

        Team winner = winnerChoiceHandler.proclaimWinner(teamId, organizerId, hackathonId);
        return ResponseEntity.ok("Winner team is " + winner.getName());
    }

    /**
     * Restituisce la lista di tutti i team partecipanti a un hackathon.
     *
     * @param organizerId l'identificatore UUID dell'organizzatore autenticato
     * @param hackathonId l'identificatore UUID dell'hackathon
     * @return ResponseEntity contenente la lista degli UUID dei team
     */
    @GetMapping("/{hackathonId}/allTeams")
    public ResponseEntity<List<UUID>> getAllTeams(
            @AuthenticationPrincipal UUID organizerId,
            @PathVariable UUID hackathonId) {

        return ResponseEntity.ok(winnerChoiceHandler.getAllTeams(organizerId, hackathonId));
    }

}