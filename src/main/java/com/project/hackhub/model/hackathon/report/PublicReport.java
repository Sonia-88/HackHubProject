/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.model.hackathon.report;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.hackhub.model.hackathon.Money;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.hackathon.state.HackathonState;
import com.project.hackhub.model.team.Team;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

/**
 * Vista di report pubblica con informazioni limitate e visibili a chiunque.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class PublicReport extends Report {

    private final Map<String, Float> teamsGrades;

    /**
     * Costruisce un report pubblico con i dettagli generali e i voti dei team.
     *
     * @param name nome dell'hackathon
     * @param ruleBook regolamento
     * @param reservation prenotazione
     * @param expiredSubscriptionsDate data di scadenza delle iscrizioni
     * @param state stato dell'hackathon
     * @param moneyPrize premio in denaro
     * @param maxTeamDimension dimensione massima del team
     * @param winner team vincitore
     * @param teamsGrades mappa dei voti dei team
     */
    public PublicReport(String name, String ruleBook, Reservation reservation, LocalDate expiredSubscriptionsDate, HackathonState state, Money moneyPrize, int maxTeamDimension, Team winner, Map<String, Float> teamsGrades) {
        super(name, ruleBook, reservation, expiredSubscriptionsDate, state, moneyPrize, maxTeamDimension, winner);
        this.teamsGrades = teamsGrades;
    }
}