/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.dto.HackathonDTO;
import com.project.hackhub.handler.HackathonHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Boundary per le operazioni di gestione degli hackathon.
 * Gestisce l'eliminazione, la modifica dei dati dell'hackathon e i cambi di ruolo dello staff.
 */
@RestController
@RequestMapping("/api/hackathon")
@AllArgsConstructor
public class HackathonBoundary {

    private final HackathonHandler hackathonHandler;

    /**
     * Elimina un hackathon.
     *
     * @param deleterId   l'utente autenticato (deve possedere i permessi di cancellazione)
     * @param hackathonId l'ID dell'hackathon da eliminare
     * @return una {@link ResponseEntity} con conferma dell'avvenuta eliminazione
     * @throws IllegalArgumentException      se l'utente o l'hackathon non esistono
     * @throws UnsupportedOperationException se l'utente non possiede i permessi
     */
    @DeleteMapping("/{hackathonId}")
    public ResponseEntity<String> deleteHackathon(
            @AuthenticationPrincipal UUID deleterId,
            @PathVariable UUID hackathonId) {
        hackathonHandler.deleteHackathon(deleterId, hackathonId);
        return ResponseEntity.ok("Hackathon deleted");
    }

    /**
     * Aggiorna i dati di base di un hackathon. La prenotazione non viene mai modificata.
     *
     * @param editorId    l'editor autenticato
     * @param hackathonId l'ID dell'hackathon
     * @param dto         il DTO contenente i nuovi dati
     * @return una {@link ResponseEntity} con conferma dell'aggiornamento
     * @throws UnsupportedOperationException se l'utente non possiede i permessi
     */
    @PutMapping("/{hackathonId}")
    public ResponseEntity<String> updateHackathon(
            @AuthenticationPrincipal UUID editorId,
            @PathVariable UUID hackathonId,
            @RequestBody HackathonDTO dto) {
        hackathonHandler.updateHackathon(editorId, hackathonId, dto);
        return ResponseEntity.ok("Hackathon successfully updated");
    }

}