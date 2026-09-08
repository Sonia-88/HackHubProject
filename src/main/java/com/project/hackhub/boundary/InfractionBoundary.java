/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.dto.InfractionDTO;
import com.project.hackhub.handler.InfractionHandler;
import com.project.hackhub.model.team.Infraction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per la gestione delle infrazioni all'interno degli eventi hackathon.
 * Espone endpoint per la segnalazione, la gestione, la penalizzazione e l'eliminazione delle infrazioni.
 */
@RestController
@RequestMapping("api/infraction")
@RequiredArgsConstructor
public class InfractionBoundary {

    private final InfractionHandler infractionHandler;

    /**
     * Segnala una nuova infrazione per un team.
     *
     * @param mentor UUID del mentor autenticato che segnala l'infrazione
     * @param dto DTO contenente i dettagli dell'infrazione
     * @return ResponseEntity con stato HTTP 200 OK
     */
    @PostMapping("/report")
    public ResponseEntity<String> reportInfraction(
            @AuthenticationPrincipal UUID mentor,
            @RequestBody InfractionDTO dto) {

        infractionHandler.reportInfraction(mentor, dto);
        return ResponseEntity.ok("infraction has been successfully reported");
    }

    /**
     * Espelle un team dall'hackathon.
     *
     * @param coordinator UUID del coordinatore autenticato
     * @param team UUID del team da espellere
     * @return messaggio di conferma
     */
    @DeleteMapping("/{team}/expel")
    public ResponseEntity<String> expelTeam(
            @AuthenticationPrincipal UUID coordinator,
            @PathVariable UUID team) {

        infractionHandler.expelTeam(team, coordinator);
        return ResponseEntity.ok("Team has been successfully expelled");
    }

    /**
     * Penalizza un team sottraendo un numero specificato di punti.
     *
     * @param coordinator UUID del coordinatore autenticato
     * @param team UUID del team da penalizzare
     * @param pointsToDeduct numero di punti da sottrarre
     * @return messaggio di conferma
     */
    @PatchMapping("/{team}/penalize")
    public ResponseEntity<String> penalizeTeam(
            @AuthenticationPrincipal UUID coordinator,
            @PathVariable UUID team,
            @RequestBody float pointsToDeduct) {

        infractionHandler.penalizeTeam(coordinator, team, pointsToDeduct);
        return ResponseEntity.ok("Team will be penalized by deducting " + pointsToDeduct + " points from final grade");
    }

    /**
     * Gestisce un'infrazione per un team.
     * Il coordinatore può decidere se penalizzare o espellere il team.
     *
     * @param coordinator UUID del coordinatore autenticato
     * @param team UUID del team con l'infrazione
     * @return lista delle infrazioni
     */
    @PostMapping("/handle")
    public ResponseEntity<List<Infraction>> handleInfraction(
            @AuthenticationPrincipal UUID coordinator,
            @RequestBody UUID team) {

        List<Infraction> infractions = infractionHandler.handleInfraction(coordinator, team);
        return ResponseEntity.ok(infractions);
    }

    /**
     * Elimina una specifica infrazione da un hackathon.
     *
     * @param userId UUID dell'utente autenticato
     * @param hackathonId UUID dell'hackathon
     * @param infractionIndex indice dell'infrazione da rimuovere
     * @return ResponseEntity con stato HTTP 200 OK
     */
    @DeleteMapping("/{hackathonId}/{infractionIndex}")
    public ResponseEntity<String> deleteInfraction(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID hackathonId,
            @PathVariable int infractionIndex) {

        infractionHandler.deleteInfraction(userId, hackathonId, infractionIndex);
        return ResponseEntity.ok("Infraction deleted");
    }
}