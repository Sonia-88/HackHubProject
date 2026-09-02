/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.handler.InfoHandler;
import com.project.hackhub.model.hackathon.report.Report;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per il recupero delle informazioni generali e dei report degli hackathon.
 */
@RestController
@RequestMapping("/api/info")
public class InfoBoundary {

    private final InfoHandler infoHandler;

    public InfoBoundary(InfoHandler infoHandler) {
        this.infoHandler = infoHandler;
    }

    /**
     * Endpoint per ottenere l'elenco di tutti gli hackathon.
     * @return una lista contenente tutti gli ID degli hackathon
     */
    @GetMapping("/hackathons")
    public ResponseEntity<List<UUID>> getAllHackathons() {
        return ResponseEntity.ok(infoHandler.getAllHackathons());
    }

    /**
     * Endpoint per ottenere il report di un determinato hackathon in base allo stato
     * dell'evento e ai permessi dell'utente.
     * @param hackathonId l'id dell'hackathon di interesse
     * @param userId l'id dell'utente che effettua l'azione
     * @return un report conforme allo stato dell'hackathon e ai permessi dell'utente
     */
    @GetMapping("/{hackathonId}/report")
    public ResponseEntity<Report> getHackathonReport(@PathVariable UUID hackathonId,
                                                     @AuthenticationPrincipal UUID userId){
        return ResponseEntity.ok(infoHandler.getHackathonReport(hackathonId, userId));
    }
}