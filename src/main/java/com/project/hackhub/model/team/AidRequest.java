/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.team;
 

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.service.calendar.Slot;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Rappresenta una richiesta di supporto (aiuto) avanzata da un team durante l'hackathon.
 */
@Embeddable
@Getter
@NoArgsConstructor
public class AidRequest {

    private String description;
    private AidRequestType type;
    @Embedded
    private Slot slot;

    @JsonIgnoreProperties({"hackathon", "invitationList", "teamMembersList", "teamLeader", "hasPendingCallProposal", "grade"})
    @ManyToOne @Getter
    private Team team;

    /**
     * Costruisce una richiesta di supporto completa con team, tipo, descrizione e slot temporale.
     *
     * @param t il team che invia la richiesta
     * @param type la tipologia di richiesta
     * @param description la descrizione testuale
     * @param slot lo slot temporale associato
     * @throws IllegalArgumentException se il team o il tipo sono nulli
     */
    public AidRequest(Team t, AidRequestType type, String description, Slot slot){
        if (t == null || type == null)
            throw new IllegalArgumentException("Invalid parameters.");
        this.team = t;
        this.type = type;
        this.description = description;
        this.slot = slot;
    }

    /**
     * Costruisce una richiesta di supporto con team, tipo e slot temporale (senza descrizione).
     *
     * @param t il team che invia la richiesta
     * @param type la tipologia di richiesta
     * @param slot lo slot temporale associato
     */
    public AidRequest(Team t, AidRequestType type, Slot slot){
        this(t, type, null, slot);
    }

    /**
     * Restituisce l'hackathon associato alla richiesta tramite il team.
     *
     * @return l'oggetto {@link Hackathon}
     * @throws IllegalStateException se il team non è associato
     */
    @JsonIgnore
    public Hackathon getHackathon() {
        if (team == null)
            throw new IllegalStateException("Team not associated.");
        return team.getHackathon();
    }
}