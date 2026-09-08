/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.dto.SubmissionDTO;
import com.project.hackhub.handler.SubmissionHandler;
import com.project.hackhub.model.team.Submission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST per la gestione delle sottomissioni dei progetti da parte dei team.
 */
@RestController
@RequestMapping("/api/submission")
public class SubmissionBoundary {
    private final SubmissionHandler submissionHandler;

    public SubmissionBoundary(SubmissionHandler submissionHandler) {
        this.submissionHandler = submissionHandler;
    }

    /**
     * Invia una nuova sottomissione per il team.
     *
     * @param teamLeader l'identificatore UUID del team leader autenticato
     * @param dto il DTO contenente i dettagli della sottomissione
     * @return ResponseEntity con stato HTTP 201 Created
     */
    @PostMapping("/send")
    public ResponseEntity<Void> sendSubmission(
            @AuthenticationPrincipal UUID teamLeader,
            @RequestBody SubmissionDTO dto) {
        submissionHandler.sendSubmission(teamLeader, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Recupera tutte le sottomissioni dei team per un dato hackathon (riservato allo staff).
     *
     * @param user l'identificatore UUID dell'utente dello staff autenticato
     * @param hackathonId l'identificatore UUID dell'hackathon
     * @return ResponseEntity con la lista delle sottomissioni o stato 204 No Content se vuota
     */
    @GetMapping("/all")
    public ResponseEntity<List<Submission>> getAllTeamsSubmissions(
            @AuthenticationPrincipal UUID user,
            @RequestBody UUID hackathonId){
        List<Submission> submissions = submissionHandler.getAllTeamsSubmissions(user, hackathonId);
        if(submissions.isEmpty())
            return ResponseEntity.noContent().build(); //204
        return ResponseEntity.ok(submissions); //200 OK
    }
}