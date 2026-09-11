/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.report;
 

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.hackhub.model.hackathon.Money;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.hackathon.state.HackathonState;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * Vista di report dettagliata destinata agli utenti che possiedono i permessi di team.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class DetailedReport extends Report {

    @JsonIgnoreProperties({"passwordHash", "organizer"})
    private final User coordinator;

    @JsonIgnoreProperties({"passwordHash", "organizer"})
    private final User judge;

    @JsonIgnoreProperties({"passwordHash", "organizer"})
    private final List<User> mentors;

    /**
     * Costruisce un report dettagliato con le informazioni estese dell'hackathon.
     *
     * @param name nome dell'hackathon
     * @param ruleBook regolamento
     * @param reservation prenotazione
     * @param expiredSubscriptionsDate data di scadenza delle iscrizioni
     * @param state stato dell'hackathon
     * @param moneyPrize premio in denaro
     * @param maxTeamDimension dimensione massima del team
     * @param winner team vincitore
     * @param coordinator coordinatore dell'evento
     * @param judge giudice dell'evento
     * @param mentors lista dei mentor
     */
    public DetailedReport(String name, String ruleBook, Reservation reservation, LocalDate expiredSubscriptionsDate, HackathonState state, Money moneyPrize, int maxTeamDimension, Team winner, User coordinator, User judge, List<User> mentors) {
        super(name, ruleBook, reservation, expiredSubscriptionsDate, state, moneyPrize, maxTeamDimension, winner);
        this.coordinator = coordinator;
        this.judge = judge;
        this.mentors = mentors;
    }
}