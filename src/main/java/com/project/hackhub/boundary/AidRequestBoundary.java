/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.dto.AidRequestDTO;
import com.project.hackhub.handler.AidRequestHandler;
import com.project.hackhub.model.team.AidRequest;
import com.project.hackhub.service.calendar.Slot;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Boundary per le operazioni di richiesta di supporto.
 * Permette ai mentor e agli organizzatori di visualizzare ed eliminare le richieste di aiuto.
 */
@RestController
@RequestMapping("/api/support")
@AllArgsConstructor
public class AidRequestBoundary {

    private final AidRequestHandler aidRequestHandler;

    /**
     * Poiché l'interazione con il sistema esterno Calendar è simulata,
     * potrebbero verificarsi inconsistenze durante i test.
     *
     * @param user l'utente autenticato
     * @param hackathon l'identificatore dell'hackathon
     * @return lista degli slot disponibili
     */
    @GetMapping("/available-slots/{hackathon}")
    public ResponseEntity<List<Slot>> getAvailableSlots(
            @AuthenticationPrincipal UUID user,
            @PathVariable UUID hackathon){
        return ResponseEntity.ok(aidRequestHandler.getAvailableSlots(user, hackathon));
    }

    /**
     * Poiché l'interazione con il sistema esterno Calendar è simulata,
     * potrebbero verificarsi inconsistenze durante i test.
     *
     * @param mentor il mentor autenticato
     * @param slot lo slot temporale proposto
     * @param team il team destinatario
     * @return conferma della proposta di chiamata
     */
    @PostMapping("/propose-call/{team}")
    public ResponseEntity<String> proposeCall(
            @AuthenticationPrincipal UUID mentor,
            @RequestBody Slot slot,
            @PathVariable UUID team
    ){
        aidRequestHandler.proposeCall(mentor, slot, team);
        return ResponseEntity.ok("call proposed");
    }

    /**
     * Invia una richiesta di supporto da parte del team leader.
     *
     * @param leader il team leader autenticato
     * @param dto il DTO della richiesta di aiuto
     * @return conferma di invio avvenuto
     */
    @PostMapping("/send-aid-request")
    public ResponseEntity<String> sendAidRequest(
            @AuthenticationPrincipal UUID leader,
            @RequestBody AidRequestDTO dto) {
        aidRequestHandler.sendAidRequest(leader, dto);
        return ResponseEntity.ok("aid request sent");
    }

    /**
     * Recupera tutte le richieste di supporto per uno specifico hackathon.
     *
     * @param userId      l'utente autenticato (deve essere mentor o organizzatore)
     * @param hackathonId l'ID dell'hackathon
     * @return una {@link ResponseEntity} contenente la lista di {@link AidRequest}
     * @throws UnsupportedOperationException se l'utente non possiede i permessi
     */
    @GetMapping("/{hackathonId}")
    public ResponseEntity<List<AidRequest>> getAidRequests(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID hackathonId) {
        List<AidRequest> requests = aidRequestHandler.getAllAidRequests(userId, hackathonId);
        return ResponseEntity.ok(requests);
    }

    /**
     * Elimina la richiesta di supporto associata a un team in un hackathon.
     *
     * @param userId      l'utente autenticato (team leader o mentor)
     * @param hackathonId l'ID dell'hackathon
     * @param teamId      l'ID del team
     * @return una {@link ResponseEntity} con conferma dell'eliminazione
     * @throws UnsupportedOperationException se l'utente non possiede i permessi
     * @throws IllegalArgumentException      se non esiste alcuna richiesta attiva per il team
     */
    @DeleteMapping("/{hackathonId}/teams/{teamId}")
    public ResponseEntity<String> deleteAidRequest(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID hackathonId,
            @PathVariable UUID teamId) {
        aidRequestHandler.deleteAidRequest(userId, hackathonId, teamId);
        return ResponseEntity.ok("support request deleted");
    }
}