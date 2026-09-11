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
import com.project.hackhub.model.team.AidRequest;
import com.project.hackhub.model.team.Infraction;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Vista di report completa destinata ai membri dello staff.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class StaffReport extends Report {

    private final List<Team> teams;

    @JsonIgnoreProperties({"passwordHash", "organizer"})
    private final List<User> mentors;

    @JsonIgnoreProperties({"passwordHash", "organizer"})
    private final User coordinator;

    @JsonIgnoreProperties({"passwordHash", "organizer"})
    private final User judge;

    private final Map<String, Float> teamsGrades;
    private final List<AidRequest> aidRequests;
    private final List<Infraction> infractions;

    /**
     * Costruisce un report completo per lo staff contenente tutti i dati amministrativi e di controllo.
     *
     * @param name nome dell'hackathon
     * @param ruleBook regolamento
     * @param reservation prenotazione
     * @param state stato dell'hackathon
     * @param moneyPrize premio in denaro
     * @param maxTeamDimension dimensione massima del team
     * @param winner team vincitore
     * @param teams lista dei team
     * @param mentors lista dei mentor
     * @param coordinator coordinatore
     * @param judge giudice
     * @param teamsGrades voti dei team
     * @param aidRequests richieste di supporto
     * @param expiredSubscriptionsDate data di scadenza delle iscrizioni
     * @param infractions infrazioni registrate
     */
    public StaffReport(String name, String ruleBook, Reservation reservation, HackathonState state, Money moneyPrize, int maxTeamDimension, Team winner, List<Team> teams, List<User> mentors, User coordinator, User judge, Map<String, Float> teamsGrades, List<AidRequest> aidRequests, LocalDate expiredSubscriptionsDate, List<Infraction> infractions) {
        super(name, ruleBook, reservation, expiredSubscriptionsDate, state, moneyPrize, maxTeamDimension, winner);
        this.teams = teams;
        this.mentors = mentors;
        this.coordinator = coordinator;
        this.judge = judge;
        this.teamsGrades = teamsGrades;
        this.aidRequests = aidRequests;
        this.infractions = infractions;
    }
}