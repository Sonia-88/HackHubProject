/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.model.hackathon.report;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.project.hackhub.model.hackathon.Money;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.hackathon.state.HackathonState;
import com.project.hackhub.model.team.Team;
import lombok.Getter;

import java.time.LocalDate;


/**
 * Classe astratta per tutte le rappresentazioni dei report dell'Hackathon.
 *
 * <p>Estesa da differenti classi finali che rappresentano gli stessi dati sottostanti
 * esposti con diversi livelli di visibilità in base ai permessi dell'utente.</p>
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Report {

    private final String name;
    private final String ruleBook;
    private final Reservation reservation;
    private final LocalDate expiredSubscriptionsDate;
    private final HackathonState state;
    private final Money moneyPrize;
    private final int maxTeamDimension;
    @JsonIncludeProperties({"name","id", "grade"})
    private final Team winner;

    /**
     * Costruisce un report di base con i campi comuni dell'hackathon.
     *
     * @param name nome dell'hackathon
     * @param ruleBook regolamento
     * @param reservation prenotazione
     * @param expiredSubscriptionsDate scadenza delle iscrizioni
     * @param state stato dell'hackathon
     * @param moneyPrize premio in denaro
     * @param maxTeamDimension dimensione massima del team
     * @param winner team vincitore
     */
    public Report(String name, String ruleBook, Reservation reservation, LocalDate expiredSubscriptionsDate, HackathonState state, Money moneyPrize, int maxTeamDimension, Team winner) {
        this.name = name;
        this.ruleBook = ruleBook;
        this.reservation = reservation;
        this.expiredSubscriptionsDate = expiredSubscriptionsDate;
        this.state = state;
        this.moneyPrize = moneyPrize;
        this.maxTeamDimension = maxTeamDimension;
        this.winner = winner;
    }
}