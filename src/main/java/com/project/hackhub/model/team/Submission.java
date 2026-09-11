/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.model.team;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.project.hackhub.model.hackathon.Hackathon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entità JPA che rappresenta la sottomissione di un elaborato o progetto da parte di un team.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Submission {
    @Id @GeneratedValue
    private UUID ids;
    @ManyToOne
    @JsonIgnoreProperties({"hackathon", "teamMembersList", "invitationList", "hasPendingCallProposal"
            , "teamLeader"})
    private Team team;
    @Embedded
    private FileTemplate fileTemplate;

    private LocalDateTime timestamp;

    private Float grade;
    private String writtenEvaluation;

    @ManyToOne
    @JoinColumn(name = "hackathon_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIncludeProperties({"id", "name"})
    private Hackathon hackathon;

    /**
     * Costruisce una nuova sottomissione associata a un team e a un template di file.
     *
     * @param team il team che effettua la sottomissione
     * @param fileTemplate il template di file allegato
     * @throws IllegalArgumentException se il team o il file template sono nulli
     */
    public Submission(Team team, FileTemplate fileTemplate){
        if (team == null || fileTemplate == null )
            throw new IllegalArgumentException("Invalid submission: can't have null arguments");
        this.team = team;
        this.fileTemplate = fileTemplate;
        this.timestamp = LocalDateTime.now();
    }
}