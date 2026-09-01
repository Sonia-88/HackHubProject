/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.boundary;


import com.project.hackhub.dto.UpdateStaffRequestDTO;
import com.project.hackhub.handler.StaffHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Boundary per le operazioni relative allo staff.
 * Gestisce l'aggiunta e la rimozione dei mentor da un hackathon.
 */
@RestController
@RequestMapping("/api/staff")
@AllArgsConstructor
public class StaffBoundary {

    private final StaffHandler staffHandler;

    /**
     * Aggiunge un mentor a un hackathon.
     *
     * @param organizerId  l'organizzatore autenticato che esegue l'azione
     * @param hackathonId  l'ID dell'hackathon
     * @param mentorId     l'ID dell'utente da aggiungere come mentor
     * @return una {@link ResponseEntity} con stato HTTP 200 OK
     * @throws IllegalArgumentException      se uno degli ID non corrisponde a un'entità esistente
     * @throws UnsupportedOperationException se l'organizzatore non ha i permessi richiesti
     * @throws IllegalStateException         se il mentor non è disponibile per l'hackathon
     */
    @PostMapping("/{hackathonId}/mentors")
    public ResponseEntity<String> addMentor(
            @AuthenticationPrincipal UUID organizerId,
            @PathVariable UUID hackathonId,
            @RequestBody UUID mentorId) {
        staffHandler.addMentor(organizerId, hackathonId, mentorId);
        return ResponseEntity.ok("mentor has been successfully added");
    }

    /**
     * Rimuove un mentor da un hackathon.
     *
     * @param organizerId  l'organizzatore autenticato che esegue l'azione
     * @param hackathonId  l'ID dell'hackathon
     * @param mentorId     l'ID del mentor da rimuovere
     * @return una {@link ResponseEntity} con stato HTTP 200 OK
     * @throws IllegalArgumentException      se l'utente non è un mentor dell'hackathon
     * @throws UnsupportedOperationException se l'organizzatore non ha i permessi richiesti
     */
    @DeleteMapping("/{hackathonId}/mentors/{mentorId}")
    public ResponseEntity<String> removeMentor(
            @AuthenticationPrincipal UUID organizerId,
            @PathVariable UUID hackathonId,
            @PathVariable UUID mentorId) {
        staffHandler.removeMentor(organizerId, hackathonId, mentorId);
        return ResponseEntity.ok("mentor has been successfully removed");
    }

    /**
     * Modifica il ruolo di un membro dello staff all'interno di un hackathon.
     *
     * @param organizerId  l'organizzatore autenticato
     * @param hackathonId  l'ID dell'hackathon
     * @param request      contiene l'ID dell'utente target e il nuovo ruolo
     * @return una {@link ResponseEntity} con stato HTTP 200 OK
     * @throws IllegalArgumentException      se il ruolo non è valido
     * @throws UnsupportedOperationException se l'organizzatore non ha i permessi o tenta di cambiare il proprio ruolo
     */
    @PostMapping("/{hackathonId}/staff/change-role")
    public ResponseEntity<String> modifyStaff(
            @AuthenticationPrincipal UUID organizerId,
            @PathVariable UUID hackathonId,
            @RequestBody UpdateStaffRequestDTO request) {
        staffHandler.changeStaffRole(organizerId, hackathonId,
                request.toChange(), request.role());
        return ResponseEntity.ok("staff successfully modified");
    }
}