/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.dto.GradeDTO;
import com.project.hackhub.handler.GradeHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST per la gestione delle valutazioni e dei voti delle sottomissioni.
 */
@RestController
@RequestMapping("/api/evaluation")
public class GradeBoundary {
    private final GradeHandler gradeHandler;

    public GradeBoundary(GradeHandler gradeHandler) {
        this.gradeHandler = gradeHandler;
    }

    /**
     * Valuta una sottomissione specifica da parte di un giudice.
     *
     * @param judge l'identificatore UUID del giudice autenticato
     * @param submissionId l'identificatore UUID della sottomissione
     * @param evaluation il DTO contenente voto e giudizio scritto
     * @return ResponseEntity con conferma della valutazione
     */
    @PatchMapping("/submission/{submissionId}")
    public ResponseEntity<String> gradeSubmission(
            @AuthenticationPrincipal UUID judge,
            @PathVariable UUID submissionId,
            @RequestBody GradeDTO evaluation) {
        gradeHandler.gradeSubmission(judge, submissionId, evaluation);
        return ResponseEntity.ok("submission " + submissionId + " successfully graded " + evaluation.grade());
    }

    /**
     * Permette di visualizzare la valutazione ricevuta da un team.
     *
     * @param teamMemberId l'identificatore UUID del membro del team autenticato
     * @param teamId l'identificatore UUID del team
     * @return ResponseEntity contenente il testo della valutazione
     */
    @GetMapping("/{teamId}")
    public ResponseEntity<String> viewEvaluation(
            @AuthenticationPrincipal UUID teamMemberId,
            @PathVariable UUID teamId) {

        return ResponseEntity.ok(gradeHandler.viewEvaluation(teamMemberId, teamId));
    }
}