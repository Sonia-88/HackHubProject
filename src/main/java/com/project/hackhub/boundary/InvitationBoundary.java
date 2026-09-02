/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.handler.InvitationHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST per le operazioni di invito ai team.
 * Gestisce gli inviti degli utenti ai team e la cancellazione degli inviti.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/invitation")
public class InvitationBoundary {

    private final InvitationHandler invitationHandler;

    /**
     * Invia un invito a un utente per unirsi a un team.
     *
     * @param teamLeader UUID del team leader autenticato che invia l'invito
     * @param user UUID dell'utente da invitare
     * @param team UUID del team
     * @return messaggio di conferma
     */
    @PostMapping("/{team}/invite/{user}")
    public ResponseEntity<String> inviteUser(
            @AuthenticationPrincipal UUID teamLeader,
            @PathVariable UUID user,
            @PathVariable UUID team) {
        invitationHandler.inviteUser(teamLeader, user, team);
        return ResponseEntity.ok("User invited");
    }

    /**
     * Annulla un invito a un team in sospeso.
     *
     * @param invitation UUID dell'invito da cancellare
     * @param teamMember UUID del membro del team autenticato
     * @return ResponseEntity con conferma di eliminazione
     */
    @DeleteMapping("/cancel/{invitation}")
    public ResponseEntity<String> cancelInvitation(
            @PathVariable UUID invitation,
            @AuthenticationPrincipal UUID teamMember
    ) {
        invitationHandler.cancelInvitation(invitation, teamMember);
        return ResponseEntity.ok("Invitation deleted");
    }

}