/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.boundary;
 

import com.project.hackhub.dto.HackathonCreationResponse;
import com.project.hackhub.dto.HackathonDTO;
import com.project.hackhub.dto.TaskDTO;
import com.project.hackhub.handler.HackathonCreationHandler;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST per la creazione di hackathon e la gestione dei task.
 * Gestisce la creazione degli eventi hackathon e l'inserimento dei task associati.
 */
@RestController
@RequestMapping("/api/hackathon")
@AllArgsConstructor
@Validated
public class HackathonCreationBoundary {

    private final HackathonCreationHandler hackathonCreationHandler;

    /**
     * Crea un nuovo evento hackathon con i dettagli forniti.
     *
     * @param coordinator UUID del coordinatore autenticato che crea l'hackathon
     * @param dto DTO contenente i dettagli dell'hackathon (luogo, nome, date, ecc.)
     * @return ResponseEntity con HTTP 201 Created se completato, HTTP 200 OK se sospeso
     */
    @PostMapping("/creation")
    public ResponseEntity<String> createHackathon(
            @AuthenticationPrincipal UUID coordinator,
            @Valid @RequestBody HackathonDTO dto) {

        HackathonCreationResponse result =
                hackathonCreationHandler.createHackathon(dto, coordinator);

        if (result.created()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(result.message());
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(result.message());
        }
    }

    /**
     * Inserisce un nuovo task all'interno di un hackathon esistente.
     *
     * @param coordinator UUID del coordinatore autenticato
     * @param taskDTO DTO contenente i dettagli del task
     * @param hackathonId UUID dell'hackathon
     * @return ResponseEntity con stato HTTP 201 Created
     */
    @PostMapping("/{hackathonId}/task")
    public ResponseEntity<Void> insertTask(
            @AuthenticationPrincipal UUID coordinator,
            @RequestBody TaskDTO taskDTO,
            @PathVariable UUID hackathonId) {

        hackathonCreationHandler.insertTask(coordinator, taskDTO, hackathonId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}