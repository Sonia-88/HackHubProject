/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.boundary;


import com.project.hackhub.handler.PrizeHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Boundary per la riscossione del premio.
 * Permette a un membro del team vincitore di richiedere la propria quota del montepremi in denaro.
 */
@RestController
@RequestMapping("/api/prize")
@AllArgsConstructor
public class PrizeBoundary {

    private final PrizeHandler prizeHandler;

    /**
     * Riscatta il premio in denaro per l'utente autenticato in un hackathon concluso.
     *
     * @param userId      l'utente autenticato (deve appartenere al team vincitore)
     * @param hackathonId l'ID dell'hackathon concluso
     * @return una {@link ResponseEntity} con stato HTTP 200 OK
     * @throws IllegalStateException         se l'hackathon non è concluso o non ha un vincitore
     * @throws UnsupportedOperationException se l'utente non fa parte del team vincitore
     */
    @PostMapping("/{hackathonId}/claim")
    public ResponseEntity<String> claimPrize(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID hackathonId) {
        prizeHandler.claimPrize(userId, hackathonId);
        return ResponseEntity.ok("Prize claimed successfully");
    }
}