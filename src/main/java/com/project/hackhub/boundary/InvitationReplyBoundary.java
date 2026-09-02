/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.handler.InvitationReplyHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST per la gestione delle risposte agli inviti.
 * Gestisce l'accettazione e il rifiuto degli inviti ai team da parte degli utenti.
 */
@RequestMapping("/api/invitation")
@RestController
@RequiredArgsConstructor
public class InvitationReplyBoundary {

    private final InvitationReplyHandler invitationReplyHandler;

    /**
     * Accetta un invito a un team.
     *
     * @param user UUID dell'utente autenticato che accetta l'invito
     * @param invitation UUID dell'invito da accettare
     * @return messaggio di conferma
     */
    @PostMapping("/{invitation}/accept")
    public ResponseEntity<String> acceptInvitation(
            @AuthenticationPrincipal UUID user,
            @PathVariable UUID invitation) {

        invitationReplyHandler.acceptInvitation(user, invitation);
        return ResponseEntity.ok("Invitation accepted");
    }

    /**
     * Rifiuta un invito a un team.
     *
     * @param user UUID dell'utente autenticato che rifiuta l'invito
     * @param invitation UUID dell'invito da declinare
     * @return ResponseEntity con conferma di rifiuto
     */
    @DeleteMapping("/{invitation}")
    public ResponseEntity<String> declineInvitation(
            @AuthenticationPrincipal UUID user,
            @PathVariable UUID invitation) {

        invitationReplyHandler.declineInvitation(user, invitation);
        return ResponseEntity.ok("Invitation declined");
    }
}